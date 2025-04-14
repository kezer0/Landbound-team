package me.kezer0.landbound.utils;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;
import java.util.Map;

public class worldCreator implements Listener {

    private static final int TIME = 6000;
    private static final int BASE_Y = 59;
    private static final int GRID_SIZE = 7;

    public static void createIslandWorld(Player player) {
        String worldName = player.getUniqueId().toString();

        // Jeśli świat już istnieje, nie rób nic
        if (Bukkit.getWorld(worldName) != null) return;

        org.bukkit.WorldCreator wc = new org.bukkit.WorldCreator(worldName)
                .generateStructures(false)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generatorSettings("{\"layers\": [{\"block\": \"air\", \"height\": 256}], \"biome\": \"plains\"}");

        World world = Bukkit.createWorld(wc);
        world.setSpawnLocation(0, BASE_Y + 1, 0);
        world.setTime(TIME);

        generateInitialChunks(world, player);
    }

    private static void generateInitialChunks(World world, Player player) {
        File islandFile = new File("plugins/LandBound/players/" + player.getUniqueId() + "/island.yml");
        worldDataGenerator generator = new worldDataGenerator(player, islandFile);

        if (worldDataGenerator.isIslandFileEmpty(islandFile)) {
            generator.generateIslandData();
            generator.saveToConfig();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(islandFile);
        List<String> chunkRows = config.getStringList("chunks");

        if (chunkRows.isEmpty()) {
            Bukkit.getLogger().warning("[LandBound] island.yml nie zawiera danych chunków!");
            return;
        }

        ConfigurationSection biomeSection = config.getConfigurationSection("biomes");
        if (biomeSection == null) {
            Bukkit.getLogger().warning("[LandBound] Brakuje sekcji 'biomes' w island.yml!");
            return;
        }

        Map<String, Object> biomeMap = biomeSection.getValues(false);

        for (int z = 0; z < GRID_SIZE; z++) {
            String[] states = chunkRows.get(z).split(",");
            for (int x = 0; x < GRID_SIZE; x++) {
                boolean unlocked = states[x].equalsIgnoreCase("O");
                String biomeName = (String) biomeMap.getOrDefault(String.valueOf(z * GRID_SIZE + x), "PLAINS");
                generateChunk(world, x, z, unlocked, biomeName);
            }
        }
    }

    private static void generateChunk(World world, int cx, int cz, boolean unlocked, String biomeName) {
        int worldX = (cx - 3) * 16;
        int worldZ = (cz - 3) * 16;

        // Ustawienie biomu
        Biome biome;
        try {
            biome = Biome.valueOf(biomeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            biome = Biome.PLAINS;
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                world.setBiome(worldX + x, worldZ + z, biome);
            }
        }

        // Fundament
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                world.getBlockAt(worldX + x, BASE_Y, worldZ + z).setType(Material.DIRT);
            }
        }

        // Warstwy nad fundamentem
        Material topMaterial = unlocked ? Material.GRASS_BLOCK : Material.WATER;
        for (int y = BASE_Y + 1; y < BASE_Y + 5; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    world.getBlockAt(worldX + x, y, worldZ + z).setType(topMaterial);
                }
            }
        }
    }
}
