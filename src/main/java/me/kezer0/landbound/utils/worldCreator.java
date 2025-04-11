package me.kezer0.landbound.utils;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class worldCreator implements Listener {

    private static final int TIME = 6000;

    public static CompletableFuture<World> createWorld(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            String worldName = player.getUniqueId().toString();
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                WorldCreator wc = new WorldCreator(worldName)
                        .generateStructures(false)
                        .environment(World.Environment.NORMAL)
                        .type(WorldType.FLAT)
                        .generatorSettings("{\"layers\": [{\"block\": \"air\", \"height\": 256}], \"biome\": \"plains\"}");
                world = Bukkit.createWorld(wc);
                world.setSpawnLocation(0, 60, 0);
                generateInitialChunks(world, player);
            }
            world.setTime(TIME);
            return world;
        });
    }

    public static World getPlayerLand(Player player) {
        return Bukkit.getWorld(player.getUniqueId().toString());
    }

    private static void generateInitialChunks(World world, Player player) {
        File islandFile = new File("plugins/LandBound/players/" + player.getUniqueId() + "/island.yml");
        if (!islandFile.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(islandFile);
        // W configu zapisujemy planszę jako listę wierszy (tekstowych)
        List<String> chunkRows = config.getStringList("chunks");
        Map<String, Object> biomeMap = config.getConfigurationSection("bioms").getValues(false);
        int gridSize = chunkRows.size();
        for (int z = 0; z < gridSize; z++) {
            String row = chunkRows.get(z);
            String[] states = row.split(",");
            for (int x = 0; x < gridSize; x++) {
                String state = states[x].trim();
                if (state.equalsIgnoreCase("O")) {
                    // Wygeneruj odblokowany chunk
                    generateChunk(x, z, true, world, biomeMap);
                } else {
                    // Generujemy nieodblokowany chunk
                    generateChunk(x, z, false, world, biomeMap);
                }
            }
        }
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
