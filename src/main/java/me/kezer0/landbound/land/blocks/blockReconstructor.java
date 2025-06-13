package me.kezer0.landbound.land.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.kezer0.landbound.land.database.databaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import me.kezer0.landbound.items.itemRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static me.kezer0.landbound.utils.signUtil.deserializeSignText;

public class blockReconstructor {
    private static final Map<Location, ItemStack[]> doubleChestBuffer = new HashMap<>();

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
                if (block.getBlockData() instanceof Bisected bisected && bisected.getHalf() == Bisected.Half.BOTTOM) {
                    Location topLoc = block.getLocation().clone().add(0, 1, 0);
                    Block topBlock = topLoc.getBlock();
                    topBlock.setType(material, false);
                    try {
                        BlockData topData = Bukkit.createBlockData(dataString);
                        if (topData instanceof Bisected topBisected) {
                            topBisected.setHalf(Bisected.Half.TOP);
                            topBlock.setBlockData(topBisected, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    block.setBlockData(Bukkit.createBlockData(dataString), false);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Nie udało się ustawić danych bloku: " + dataString + " dla bloku: " + x + "," + y + "," + z);
                    continue;
                }

                if (customId != null && customId.startsWith("SIGN:") && block.getState() instanceof Sign sign) {
                    deserializeSignText(sign, customId);
                    sign.update();
                }

                if (itemsString != null && !itemsString.isEmpty()) {
                    if (block.getState() instanceof BlockInventoryHolder holder) {
                        Inventory inventory = holder.getInventory();
                        inventory.setContents(Objects.requireNonNull(deserializeInventory(block, itemsString, inventory.getSize())));
                    }
                    if (block.getState() instanceof ItemFrame itemFrame) {
                        ItemStack item = deserializeSingleItem(itemsString);
                        itemFrame.setItem(item);
                    }
                }
            }
            for (Map.Entry<Location, ItemStack[]> entry : doubleChestBuffer.entrySet()) {
                Block block = entry.getKey().getBlock();
                if (!(block.getState() instanceof Chest chest)) continue;

                if (chest.getInventory() instanceof DoubleChestInventory doubleInv) {
                    doubleInv.getLeftSide().setContents(entry.getValue());
                }
            }
            doubleChestBuffer.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ItemStack[] deserializeInventory(Block block, String json, int inventorySize) {
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();

        if (block.getState() instanceof Chest chest && chest.getInventory() instanceof DoubleChestInventory) {
            ItemStack[] fullContents = new ItemStack[54];

            for (var element : array) {
                if (!element.isJsonObject()) continue;
                JsonObject obj = element.getAsJsonObject();
                int slot = obj.get("slot").getAsInt();
                boolean isLeft = obj.get("isLeftSide").getAsBoolean();
                String dcId = obj.get("doubleChestId").getAsString();

                int offset = isLeft ? 0 : 27;
                fullContents[offset + (slot % 27)] = deserializeItem(obj);
            }

            Block leftBlock = block;
            if (!array.isEmpty()) {
                JsonObject first = array.get(0).getAsJsonObject();
                String[] coords = first.get("doubleChestId").getAsString().split(",");
                if (coords.length == 3) {
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    int z = Integer.parseInt(coords[2]);
                    leftBlock = block.getWorld().getBlockAt(x, y, z);
                }
            }

            doubleChestBuffer.put(leftBlock.getLocation(), fullContents);
            return null;
        }

        ItemStack[] contents = new ItemStack[inventorySize];
        for (var element : array) {
            if (!element.isJsonObject()) continue;
            JsonObject obj = element.getAsJsonObject();
            int slot = obj.get("slot").getAsInt();
            contents[slot] = deserializeItem(obj);
        }
        return contents;
    }

    private static ItemStack deserializeSingleItem(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        return deserializeItem(obj);
    }

   public static ItemStack deserializeItem(JsonObject obj) {
        if (obj == null || obj.entrySet().isEmpty()) return new ItemStack(Material.AIR);

        ItemStack item;
        if (obj.has("customId") && !obj.get("customId").isJsonNull()) {
            String customId = obj.get("customId").getAsString();
            item = getCustomItem(customId);
        } else if (obj.has("material") && !obj.get("material").isJsonNull()) {
            Material mat = Material.matchMaterial(obj.get("material").getAsString());
            int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;
            if (mat == null) mat = Material.STONE;
            item = new ItemStack(mat, amount);
        } else {
            return new ItemStack(Material.AIR);
        }

        if (obj.has("shulkerContents") && item.getItemMeta() instanceof BlockStateMeta bsm) {
            BlockState state = bsm.getBlockState();
            if (state instanceof ShulkerBox shulker) {
                JsonArray contentsArray = obj.getAsJsonArray("shulkerContents");
                ItemStack[] contents = new ItemStack[27];
                int i = 0;
                for (var element : contentsArray) {
                    if (element.isJsonObject()) {
                        contents[i++] = deserializeItem(element.getAsJsonObject());
                    }
                }
                shulker.getInventory().setContents(contents);
                shulker.update();
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
        return new ItemStack(Material.STONE);
    }
}
