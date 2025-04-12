package me.kezer0.landbound.utils;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static me.kezer0.landbound.player.configPlayer.islandFile;

public class worldCreator implements Listener {

    private static final int TIME = 6000;

    public static void createWorld(Player player) {
        String worldName = player.getUniqueId().toString();

        // Jeśli świat już istnieje – nie rób nic
        if (Bukkit.getWorld(worldName) != null) return;

        WorldCreator wc = new WorldCreator(worldName)
                .generateStructures(false)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generatorSettings("{\"layers\": [{\"block\": \"air\", \"height\": 256}], \"biome\": \"plains\"}");

        World world = Bukkit.createWorld(wc);
        world.setSpawnLocation(0, 60, 0);
        world.setTime(TIME);

        generateInitialChunks(world, player);
    }

    private static void generateInitialChunks(World world, Player player) {
        File islandFile = new File("plugins/LandBound/players/" + player.getUniqueId() + "/island.yml");

        worldGenerator generator = new worldGenerator(player);
        if (!islandFile.exists() || isIslandEmpty()) {
            generator.generateWorld();
            generator.saveToConfig();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(islandFile);
        List<String> chunkRows = config.getStringList("chunks");

        if (chunkRows.isEmpty()) {
            Bukkit.getLogger().warning("[LandBound] Plik island.yml istnieje, ale brak w nim danych o chunkach!");
            return;
        }
        ConfigurationSection biomeSection = config.getConfigurationSection("biomes");
        if (biomeSection == null) {
            Bukkit.getLogger().warning("[LandBound] Brak sekcji 'biomes' w pliku island.yml!");
            return;
        }
        Map<String, Object> biomeMap = biomeSection.getValues(false);
        int gridSize = chunkRows.size();

        for (int z = 0; z < gridSize; z++) {
            String row = chunkRows.get(z);
            String[] states = row.split(",");
            for (int x = 0; x < gridSize; x++) {
                String state = states[x].trim();
                boolean unlocked = state.equalsIgnoreCase("O");
                generateChunk(x, z, unlocked, world, biomeMap);
            }
        }
    }
    private static boolean isIslandEmpty() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(islandFile);
        return !config.contains("chunks") || config.getStringList("chunks").isEmpty();
    }
    private static void generateChunk(int cx, int cz, boolean unlocked, World world, Map<String, Object> biomeData) {
        int baseY = 59; // fundamenty
        int worldX = (cx - 3) * 16;
        int worldZ = (cz - 3) * 16;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                world.getBlockAt(worldX + x, baseY, worldZ + z).setType(Material.DIRT);
            }
        }
        if (unlocked) {
            for (int y = baseY + 1; y < baseY + 5; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        world.getBlockAt(worldX + x, y, worldZ + z).setType(Material.GRASS_BLOCK);
                    }
                }
            }
        } else {
            // Dla nieodblokowanych chunków wylewamy wodę od y=60 do 63
            for (int y = baseY + 1; y < baseY + 5; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        world.getBlockAt(worldX + x, y, worldZ + z).setType(Material.WATER);
                    }
                }
            }
        }
    }
}
