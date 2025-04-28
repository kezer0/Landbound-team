package me.kezer0.landbound.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.kezer0.landbound.database.databaseManager;
import me.kezer0.landbound.items.itemRegistry;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static me.kezer0.landbound.utils.signUtil.deserializeSignText;

public class blockReconstructor {

    public static void loadBlocks(Player player) {
        UUID uuid = player.getUniqueId();

        try (Connection conn = databaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT x, y, z, world, type, blockData, customId, items FROM blocks WHERE uuid = ?"
            );
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String worldName = rs.getString("world");
                String typeString = rs.getString("type");
                String dataString = rs.getString("blockData");
                String customId = rs.getString("customId");
                String itemsString = rs.getString("items");

                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    Bukkit.getLogger().warning("Świat nie znaleziony: " + worldName);
                    continue;
                }

                Block block = world.getBlockAt(x, y, z);
                Material material = Material.getMaterial(typeString);
                if (material == null) {
                    Bukkit.getLogger().warning("Nieprawidłowy typ bloku: " + typeString);
                    continue;
                }

                block.setType(material, false);
                try {
                    block.setBlockData(Bukkit.createBlockData(dataString), false);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Nie udało się ustawić danych bloku: " + dataString + " dla bloku: " + x + "," + y + "," + z);
                    continue;
                }

                // Odtwarzanie tabliczki
                if (customId != null && customId.startsWith("SIGN:") && block.getState() instanceof Sign sign) {
                    deserializeSignText(sign, customId);
                    sign.update();
                }

                // Odtwarzanie przedmiotów
                if (itemsString != null && !itemsString.isEmpty()) {
                    if (block.getState() instanceof BlockInventoryHolder holder) {
                        Inventory inventory = holder.getInventory();
                        inventory.setContents(deserializeInventory(itemsString, inventory.getSize()));
                    }
                    if (block.getState() instanceof ItemFrame itemFrame) {
                        ItemStack item = deserializeSingleItem(itemsString);
                        itemFrame.setItem(item);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ItemStack[] deserializeInventory(String json, int inventorySize) {
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        ItemStack[] contents = new ItemStack[inventorySize];

        for (var element : array) {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                int slot = obj.get("slot").getAsInt();
                ItemStack item = deserializeItem(obj);
                if (slot >= 0 && slot < contents.length) {
                    contents[slot] = item;
                }
            }
        }
        return contents;
    }

    private static ItemStack deserializeSingleItem(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        return deserializeItem(obj);
    }

    private static ItemStack deserializeItem(JsonObject obj) {
        ItemStack item;
        if (obj.has("customId")) {
            String customId = obj.get("customId").getAsString();
            item = getCustomItem(customId);
        } else {
            Material mat = Material.matchMaterial(obj.get("material").getAsString());
            int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;
            if (mat == null) mat = Material.STONE;
            item = new ItemStack(mat, amount);
        }

        if (obj.has("shulkerContents") && item.getItemMeta() instanceof BlockStateMeta bsm) {
            if (bsm.getBlockState() instanceof ShulkerBox shulker) {
                JsonArray contentsArray = obj.getAsJsonArray("shulkerContents");
                ItemStack[] contents = new ItemStack[27]; // Shulker ma 27 slotów
                int i = 0;
                for (var element : contentsArray) {
                    if (element.isJsonObject()) {
                        contents[i++] = deserializeItem(element.getAsJsonObject());
                    }
                }
                shulker.getInventory().setContents(contents);
                bsm.setBlockState(shulker);
                item.setItemMeta(bsm);
            }
        }

        return item;
    }

    private static ItemStack getCustomItem(String customId) {
        ItemStack item = itemRegistry.getItem(customId);
        if (item != null) {
            return item;
        }
        return new ItemStack(Material.STONE); // fallback
    }
}
