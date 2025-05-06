package me.kezer0.landbound.land.generation;

import me.kezer0.landbound.land.blocks.blockReconstructor;
import me.kezer0.landbound.land.entity.entityReconstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class worldDataGenerator {

    private static final int GRID_SIZE = 9;
    private final File islandFile;
    private final FileConfiguration config;
    private final char[][] chunkStates;
    private final UUID uuid;

    public worldDataGenerator(Player player, File islandFile) {
        this.uuid = player.getUniqueId();
        this.islandFile = islandFile;
        this.config = islandFile.exists() ? YamlConfiguration.loadConfiguration(islandFile) : new YamlConfiguration();
        this.chunkStates = new char[GRID_SIZE][GRID_SIZE];
    }

   public void generateIslandData(Player player) {

        Bukkit.getLogger().info("[LandBound] Generuję dane chunków dla gracza " + uuid);

        for (int z = 0; z < GRID_SIZE; z++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                chunkStates[x][z] = (x == 5 && z == 5) ? 'O' : 'N';
            }
        }

        saveChunkStates();

        blockReconstructor.loadBlocks(player);
        entityReconstructor.loadAllEntities();
    }

    private void saveChunkStates() {

        List<String> chunkRows = new ArrayList<>();
        for (int z = 0; z < GRID_SIZE; z++) {

            StringBuilder row = new StringBuilder();
            for (int x = 0; x < GRID_SIZE; x++) {

                row.append(chunkStates[x][z]);
                if (x != GRID_SIZE - 1) row.append(",");
            }
            chunkRows.add(row.toString());
        }

        config.set("chunks", chunkRows);
        Map<String, String> biomes = new HashMap<>();
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {

            biomes.put(String.valueOf(i), "PLAINS");
        }
        config.createSection("biomes", biomes);
    }

    public void saveToConfig() {
        try {

            config.save(islandFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[LandBound] Nie udało się zapisać island.yml dla gracza " + uuid);
            e.printStackTrace();
        }
    }

    public static boolean isIslandFileEmpty(File file) {
        if (!file.exists()) return true;
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return !config.contains("chunks") || config.getStringList("chunks").isEmpty();
    }
}
