package me.kezer0.landbound.land.blocks;

import me.kezer0.landbound.land.database.databaseManager;
import me.kezer0.landbound.utils.signUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.kezer0.landbound.land.blocks.blockSerializerHelper.serializeLocation;

public class blockSaver {
    private static final Map<UUID, Map<String, BlockData>> blockBuffer = new ConcurrentHashMap<>();

    public static void saveBlock(Block block, Player player) {
        UUID uuid = player.getUniqueId();
        String key = serializeLocation(block);
        BlockData data = blockSerializer.serialize(block, player, blockBuffer.get(uuid));
        blockBuffer.computeIfAbsent(uuid, k -> new HashMap<>()).put(key, data);
    }


    public static void removeBlock(Block block, Player player) {
        UUID uuid = player.getUniqueId();
        blockBuffer.computeIfAbsent(uuid, k -> new HashMap<>()).put(serializeLocation(block), null);
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
        blockBuffer.keySet().forEach(blockSaver::flushBufferToDisk);
    }

    public static class BlockData {
        private final Location location;
        private final Material type;
        private final String blockData;
        private final String customId;
        private final String items;

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
