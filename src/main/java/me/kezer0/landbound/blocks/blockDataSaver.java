package me.kezer0.landbound.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.kezer0.landbound.database.databaseManager;
import me.kezer0.landbound.utils.signUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class blockDataSaver {

    private static final Map<UUID, Map<String, BlockData>> blockBuffer = new ConcurrentHashMap<>();

    public static void saveBlock(Block block, Player player) {
        UUID uuid = player.getUniqueId();
        String path = serializeLocation(block);
        Material type = block.getType();
        String customId = null;
        String itemsJson = null;

        BlockState state = block.getState();
//        state.update(true, false);

        if (state instanceof Sign sign) {
            Component[] frontLines = sign.getSide(Side.FRONT).lines().toArray(new Component[0]);
            String color = sign.getColor().name();
            boolean glowingFront = sign.getSide(Side.FRONT).isGlowingText();
            customId = signUtil.serializeSignText(frontLines, color, glowingFront);
        }

        if (state instanceof BlockInventoryHolder holder) {
            ItemStack[] contents = holder.getInventory().getContents();
            boolean hasItems = false;
            for (ItemStack item : contents) {
                if (item != null && !item.getType().isAir()) {
                    hasItems = true;
                    Bukkit.getLogger().info("Zapisuję block: " + block.getType() + " itemsJson=" + itemsJson);
                    break;
                }
            }
            if (hasItems) {
                itemsJson = serializeInventory(contents);
            }
        }

        String blockDataString = state.getBlockData().getAsString();

        BlockData data = new BlockData(block.getLocation(), type, blockDataString, customId, itemsJson);
        blockBuffer.computeIfAbsent(uuid, k -> new HashMap<>()).put(path, data);
    }



    public static void removeBlock(Block block, Player player) {
        UUID uuid = player.getUniqueId();
        String path = serializeLocation(block);
        blockBuffer.computeIfAbsent(uuid, k -> new HashMap<>()).put(path, null);
    }

    public static void updateBlock(Block block) {
        for (UUID uuid : blockBuffer.keySet()) {
            Map<String, BlockData> playerBuffer = blockBuffer.get(uuid);
            if (playerBuffer == null) continue;

            String path = serializeLocation(block);
            if (playerBuffer.containsKey(path)) {
                BlockData oldData = playerBuffer.get(path);
                if (oldData != null) {
                    BlockState state = block.getState();
                    state.update(true, false);

                    String customId = oldData.getCustomId();
                    String items = oldData.getItems();

                    if (state instanceof Sign sign) {
                        Component[] frontLines = sign.getSide(Side.FRONT).lines().toArray(new Component[0]);
                        String color = sign.getColor().name();
                        boolean glowingFront = sign.getSide(Side.FRONT).isGlowingText();

                        customId = signUtil.serializeSignText(frontLines, color, glowingFront);
                    }

                    playerBuffer.put(path, new BlockData(
                            block.getLocation(),
                            block.getType(),
                            block.getBlockData().getAsString(),
                            customId,
                            items
                    ));
                }
            }
        }
    }

    private static String serializeInventory(ItemStack[] contents) {
        JsonArray array = new JsonArray();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item == null || item.getType().isAir()) continue;

            JsonObject obj = new JsonObject();
            obj.addProperty("slot", slot);

            String customId = getCustomId(item);
            if (customId != null) {
                obj.addProperty("customId", customId);
            } else {
                obj.addProperty("material", item.getType().name());
            }
            obj.addProperty("amount", item.getAmount());
            array.add(obj);
        }
        return array.toString();
    }

    private static String serializeSingleItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return "";
        return serializeItem(item).toString();
    }

    private static JsonObject serializeItem(ItemStack item) {
        JsonObject obj = new JsonObject();
        String customId = getCustomId(item);
        if (customId != null) {
            obj.addProperty("customId", customId);
        } else {
            obj.addProperty("material", item.getType().name());
        }
        obj.addProperty("amount", item.getAmount());

        // Dodajemy obsługę ShulkerBoxów
        if (item.getItemMeta() instanceof BlockStateMeta bsm && bsm.getBlockState() instanceof ShulkerBox shulker) {
            ItemStack[] contents = shulker.getInventory().getContents();
            JsonArray shulkerArray = new JsonArray();
            for (ItemStack shulkerItem : contents) {
                if (shulkerItem == null || shulkerItem.getType().isAir()) continue;
                shulkerArray.add(serializeItem(shulkerItem));
            }
            obj.add("shulkerContents", shulkerArray);
        }

        return obj;
    }

    private static String getCustomId(ItemStack item) {
        if (item.hasItemMeta()) {
            var meta = item.getItemMeta();
            NamespacedKey key = new NamespacedKey("landbound", "custom_id");
            if (meta.getPersistentDataContainer().has(key, org.bukkit.persistence.PersistentDataType.STRING)) {
                return meta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.STRING);
            }
        }
        return null;
    }
    public static void flushBufferToDisk(UUID uuid) {
        Map<String, BlockData> buffer = blockBuffer.get(uuid);
        if (buffer == null || buffer.isEmpty()) return;

        try (Connection conn = databaseManager.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT OR REPLACE INTO blocks (uuid, x, y, z, world, type, blockData, customId, items) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            PreparedStatement deleteStmt = conn.prepareStatement(
                "DELETE FROM blocks WHERE uuid = ? AND x = ? AND y = ? AND z = ?"
            );

            for (Map.Entry<String, BlockData> entry : buffer.entrySet()) {
                String key = entry.getKey();
                BlockData data = entry.getValue();

                String[] parts = key.split(",");
                if (parts.length != 3) continue;

                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int z = Integer.parseInt(parts[2]);

                if (data == null) {
                    deleteStmt.setString(1, uuid.toString());
                    deleteStmt.setInt(2, x);
                    deleteStmt.setInt(3, y);
                    deleteStmt.setInt(4, z);
                    deleteStmt.addBatch();
                } else {
                    Location loc = data.getLocation();
                    insertStmt.setString(1, uuid.toString());
                    insertStmt.setInt(2, x);
                    insertStmt.setInt(3, y);
                    insertStmt.setInt(4, z);
                    insertStmt.setString(5, loc.getWorld().getName());
                    insertStmt.setString(6, data.getType().name());
                    insertStmt.setString(7, data.getBlockData());
                    insertStmt.setString(8, data.getCustomId());
                    insertStmt.setString(9, data.getItems());
                    insertStmt.addBatch();
                }
            }

            deleteStmt.executeBatch();
            insertStmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        blockBuffer.remove(uuid);
    }

    public static void flushAll() {
        for (UUID uuid : blockBuffer.keySet()) {
            flushBufferToDisk(uuid);
        }
    }

    private static String serializeLocation(Block block) {
        return block.getX() + "," + block.getY() + "," + block.getZ();
    }
    public static class BlockData {
        private final Location location;
        private final Material type;
        private final String blockData;
        private final String customId;
        private final String items; // Dodane

        public BlockData(Location location, Material type, String blockData, String customId, String items) {
            this.location = location;
            this.type = type;
            this.blockData = blockData;
            this.customId = customId;
            this.items = items;
        }

        public Location getLocation() {
            return location;
        }

        public Material getType() {
            return type;
        }

        public String getBlockData() {
            return blockData;
        }

        public String getCustomId() {
            return customId;
        }

        public String getItems() {
            return items;
        }
    }
}
