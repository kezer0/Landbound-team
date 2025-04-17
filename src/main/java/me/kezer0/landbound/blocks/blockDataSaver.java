package me.kezer0.landbound.blocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class blockDataSaver {

    private static final Map<UUID, Map<String, blockData>> blockBuffer = new HashMap<>();

    public static void saveBlock(Block block, Player player) {
        UUID uuid = player.getUniqueId();
        String path = serializeLocation(block);
        Material type = block.getType();
        String customId = null;

        if (block.getState() instanceof Sign sign) {
            StringBuilder contentBuilder = new StringBuilder();
            String[] lines = sign.getLines();

            for (int i = 0; i < 4; i++) {
                String line = lines[i] != null ? lines[i] : "";
                contentBuilder.append(line);
                if (i < 3) contentBuilder.append(";;");
            }

            String content = contentBuilder.toString();
            String color = sign.getColor().name();
            boolean glowing = sign.isGlowingText();

            customId = "SIGN:" + content + ";color=" + color + ";glowing=" + glowing;
        }

        String blockDataString = block.getBlockData().getAsString();
        blockData data = new blockData(type, blockDataString, customId);

        blockBuffer.computeIfAbsent(uuid, k -> new HashMap<>()).put(path, data);
    }

    public static void removeBlock(Block block, Player player) {
        UUID uuid = player.getUniqueId();
        String path = serializeLocation(block);

        Bukkit.getLogger().info("Próba usunięcia bloku: " + path);
        blockBuffer.computeIfAbsent(uuid, k -> new HashMap<>()).put(path, null);
        Bukkit.getLogger().info("Zaznaczono do usunięcia blok: " + path);
    }

    public static void flushBufferToDisk(UUID uuid) {
        Map<String, blockData> buffer = blockBuffer.get(uuid);
        if (buffer == null || buffer.isEmpty()) return;

        File file = blockDataManager.getBlockDataFile(uuid);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        Bukkit.getLogger().info("Zapis danych bloków: " + buffer.size() + " pozycji dla gracza " + uuid);

        for (Map.Entry<String, blockData> entry : buffer.entrySet()) {
            String key = entry.getKey();
            blockData data = entry.getValue();

            if (data == null) {
                config.set(key, null);
                continue;
            }

            Location loc = parseLocationKey(uuid, key);
            if (loc == null) continue;

            config.set(key + ".type", data.getType().name());
            config.set(key + ".data", data.getBlockData());
            config.set(key + ".world", loc.getWorld().getName());

            if (data.getCustomId() != null) {
                config.set(key + ".custom_id", data.getCustomId());
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        blockBuffer.remove(uuid);
    }

    private static Location parseLocationKey(UUID uuid, String key) {
        String[] parts = key.split(",");
        if (parts.length != 3) return null;

        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);
            World world = Bukkit.getWorld(uuid.toString());
            if (world == null) return null;
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void flushAll() {
        for (UUID uuid : blockBuffer.keySet()) {
            flushBufferToDisk(uuid);
        }
    }

    private static String serializeLocation(Block block) {
        return block.getX() + "," + block.getY() + "," + block.getZ();
    }

    public static class blockData {
        private final Material type;
        private final String blockData;
        private final String customId;

        public blockData(Material type, String blockData, String customId) {
            this.type = type;
            this.blockData = blockData;
            this.customId = customId;
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
    }
}
