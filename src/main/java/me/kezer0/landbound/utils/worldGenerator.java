package me.kezer0.landbound.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class worldGenerator {

    private static final int GRID_SIZE = 7;
    private char[][] chunkStates;
    private BiomeDefinition[][] biomeMap;
    private long seed;
    private File islandFile;
    private FileConfiguration config;

    public static class BiomeDefinition {
        private final String name;
        private final int temperature; // 1 = ciepły, 0 = neutralny, -1 = zimny

        public BiomeDefinition(String name, int temperature) {
            this.name = name;
            this.temperature = temperature;
        }
        public String getName() {
            return name;
        }
        public int getTemperature() {
            return temperature;
        }
    }

    // Dostępne biomy
    private BiomeDefinition[] availableBiomes = new BiomeDefinition[] {
            new BiomeDefinition("desert", 1),
            new BiomeDefinition("savanna", 1),
            new BiomeDefinition("plains", 0),
            new BiomeDefinition("forest", 0),
            new BiomeDefinition("tundra", -1)
    };

    public worldGenerator(File islandFile, long seed) {
        this.islandFile = islandFile;
        this.seed = seed;
        config = YamlConfiguration.loadConfiguration(islandFile);
        chunkStates = new char[GRID_SIZE][GRID_SIZE];
        biomeMap = new BiomeDefinition[GRID_SIZE][GRID_SIZE];
    }

    public void generateWorld() {
        Random random = new Random(seed);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (i == 3 && j == 3) {
                    chunkStates[i][j] = 'O';
                } else {
                    chunkStates[i][j] = 'N';
                }
            }
        }
        // Generowanie biomów
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                List<BiomeDefinition> possibleBiomes = getAllowedBiomes(i, j);
                if (possibleBiomes.isEmpty()) {
                    possibleBiomes.add(new BiomeDefinition("plains", 0));
                }
                BiomeDefinition chosenBiome = possibleBiomes.get(random.nextInt(possibleBiomes.size()));
                biomeMap[i][j] = chosenBiome;
            }
        }
        ensureBiomePresence(random, 1);
        ensureBiomePresence(random, -1);
    }

    private List<BiomeDefinition> getAllowedBiomes(int i, int j) {
        List<BiomeDefinition> candidates = new ArrayList<>();
        boolean hasWarmNeighbor = false;
        boolean hasColdNeighbor = false;
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) continue;
                int ni = i + di;
                int nj = j + dj;
                if (ni >= 0 && ni < GRID_SIZE && nj >= 0 && nj < GRID_SIZE && biomeMap[ni][nj] != null) {
                    int neighborTemp = biomeMap[ni][nj].getTemperature();
                    if (neighborTemp == 1) hasWarmNeighbor = true;
                    if (neighborTemp == -1) hasColdNeighbor = true;
                }
            }
        }
        for (BiomeDefinition biome : availableBiomes) {
            if (hasWarmNeighbor && biome.getTemperature() == -1) continue;
            if (hasColdNeighbor && biome.getTemperature() == 1) continue;
            candidates.add(biome);
        }
        return candidates;
    }

    private void ensureBiomePresence(Random random, int requiredTemp) {
        boolean found = false;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (biomeMap[i][j].getTemperature() == requiredTemp) {
                    found = true;
                    break;
                }
            }
            if (found) break;
        }
        if (!found) {
            for (int trial = 0; trial < 100; trial++) {
                int i = random.nextInt(GRID_SIZE);
                int j = random.nextInt(GRID_SIZE);
                List<BiomeDefinition> candidates = getAllowedBiomes(i, j);
                for (BiomeDefinition biome : candidates) {
                    if (biome.getTemperature() == requiredTemp) {
                        biomeMap[i][j] = biome;
                        return;
                    }
                }
            }
        }
    }

    public void saveToConfig() {
        StringBuilder chunkBuilder = new StringBuilder();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                chunkBuilder.append(chunkStates[i][j]);
                if (j < GRID_SIZE - 1) chunkBuilder.append(",");
            }
            if (i < GRID_SIZE - 1) chunkBuilder.append("\n");
        }
        config.set("chunks", chunkBuilder.toString());
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int index = i * GRID_SIZE + j;
                config.set("bioms." + index, biomeMap[i][j].getName());
            }
        }
        try {
            config.save(islandFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
