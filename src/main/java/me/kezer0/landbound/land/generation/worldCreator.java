package me.kezer0.landbound.land.generation;

import me.kezer0.landbound.Landbound;
import me.kezer0.landbound.land.blocks.blockListener;
import me.kezer0.landbound.land.blocks.blockReconstructor;
import me.kezer0.landbound.land.config.configLand;
import me.kezer0.landbound.land.entity.entityDataListener;
import me.kezer0.landbound.land.entity.entityReconstructor;
import me.kezer0.landbound.land.listeners.boundaryListener;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class worldCreator {

    private static final int TIME = 6000;
    private static final int BASE_Y = 59;
    private static final int GRID_SIZE = 9;
    private static final int CENTER = GRID_SIZE / 2;

    private static final Plugin plugin = Landbound.getPlugin(Landbound.class);

    public static void createIslandWorld(Player player) {
        String worldName = "island_" + player.getUniqueId();
        if (Bukkit.getWorld(worldName) != null) return;

        WorldCreator wc = new WorldCreator(worldName)
                .generateStructures(false)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generatorSettings("{\"layers\": [{\"block\": \"air\", \"height\": 256}], \"biome\": \"plains\"}");

        World world = Bukkit.createWorld(wc);
        world.setSpawnLocation(24, BASE_Y + 1, 24);
        world.setTime(TIME);

        File islandFile = new File("plugins/LandBound/players/" + player.getUniqueId() + "/island.yml");
        worldDataGenerator generator = new worldDataGenerator(player, islandFile);

        if (worldDataGenerator.isIslandFileEmpty(islandFile)) {
            generator.generateIslandData(player);
            generator.saveToConfig();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(islandFile);
        List<String> chunkRows = config.getStringList("chunks");

        if (chunkRows.isEmpty()) {
            Bukkit.getLogger().warning("[LandBound] island.yml nie zawiera danych chunków!");
            return;
        }

        new BukkitRunnable() {
            int z = 0;

            @Override
            public void run() {
                if (z >= GRID_SIZE) {
                    blockReconstructor.loadBlocks(player);
                    entityReconstructor.loadAllEntities();

                    Location spawn = new Location(world, 24, BASE_Y + 5, 24);
                    player.teleportAsync(spawn);
                    player.setGameMode(GameMode.SURVIVAL);
                    cancel();
                    return;
                }

                String[] states = chunkRows.get(z).split(",");
                for (int x = 0; x < GRID_SIZE; x++) {
                    boolean unlocked = states[x].equalsIgnoreCase("O");
                    generateChunk(world, x, z, unlocked);
                }
                z++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
        Bukkit.getPluginManager().registerEvents(new boundaryListener(),plugin);
        Bukkit.getPluginManager().registerEvents(new configLand(),plugin);
        Bukkit.getPluginManager().registerEvents(new blockListener(),plugin);
        Bukkit.getPluginManager().registerEvents(new entityDataListener(),plugin);
    }

    private static void generateChunk(World world, int cx, int cz, boolean unlocked) {
        int worldX = (cx - CENTER) * 16;
        int worldZ = (cz - CENTER) * 16;

        Chunk chunk = world.getChunkAt(worldX >> 4, worldZ >> 4);
        if (!chunk.isLoaded()) {
            chunk.load(true);
        }
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                world.getBlockAt(worldX + x, BASE_Y, worldZ + z).setType(Material.DIRT);
            }
        }
        Material topMaterial = unlocked ? Material.GRASS_BLOCK : Material.WATER;
        for (int y = BASE_Y + 1; y < BASE_Y + 5; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    world.getBlockAt(worldX + x, y, worldZ + z).setType(topMaterial);
                }
            }
        }
    }

    public static void unloadAndDeleteWorld(Player player) {
        World world = Bukkit.getWorld("island_" + player.getUniqueId());
        if (world == null) return;

        for (Player p : world.getPlayers()) {
            p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        boolean unloaded = Bukkit.unloadWorld(world, false);
        if (!unloaded) {
            Bukkit.getLogger().warning("[LandBound] Nie udało się odładować świata: " + world.getName());
            return;
        }

        File worldFolder = world.getWorldFolder();
        try {
            deleteWorldFolder(worldFolder);
            Bukkit.getLogger().info("[LandBound] Świat gracza usunięty: " + world.getName());
        } catch (IOException e) {
            Bukkit.getLogger().severe("[LandBound] Błąd przy usuwaniu folderu świata: " + e.getMessage());
        }
    }

    private static void deleteWorldFolder(File folder) throws IOException {
        if (!folder.exists()) return;

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorldFolder(file);
                } else if (!file.delete()) {
                    throw new IOException("Nie można usunąć pliku: " + file.getAbsolutePath());
                }
            }
        }
        if (!folder.delete()) {
            throw new IOException("Nie można usunąć folderu: " + folder.getAbsolutePath());
        }
    }
}
