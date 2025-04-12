package me.kezer0.landbound.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class worldGenerator {

    private static final int GRID_SIZE = 7;
    private final char[][] chunkStates;
    private final BiomeDefinition[][] biomeMap;
    private final long seed;
    private final File islandFile;
    private final FileConfiguration config;

    public record BiomeDefinition(String name, int temperature) {}

    private final BiomeDefinition[] availableBiomes = new BiomeDefinition[] {
        new BiomeDefinition("desert", 1),
        new BiomeDefinition("savanna", 1),
        new BiomeDefinition("plains", 0),
        new BiomeDefinition("forest", 0),
        new BiomeDefinition("tundra", -1)
    };

    public worldGenerator(Player player) {
        UUID uuid = player.getUniqueId();
        this.seed = uuid.getLeastSignificantBits(); // Można zmienić na cokolwiek
        this.islandFile = new File("plugins/LandBound/players/" + uuid + "/island.yml");
        this.config = islandFile.exists() ? YamlConfiguration.loadConfiguration(islandFile) : new YamlConfiguration();
        this.chunkStates = new char[GRID_SIZE][GRID_SIZE];
        this.biomeMap = new BiomeDefinition[GRID_SIZE][GRID_SIZE];
    }

    public void generateWorld() {
        if (islandFile.exists() && !config.getStringList("chunks").isEmpty()) {
            Bukkit.getLogger().info("[LandBound] Plik wyspy już istnieje i zawiera dane, pomijam generację.");
            return;
        }
        Bukkit.getLogger().info("[LandBound] Generuję nową wyspę dla gracza...");

        generateChunkStates();
        generateBiomeMap();
        Bukkit.getLogger().info("[LandBound] poprawnie wygenerowano chunki i  biomy");
        ensureBiomePresence(new Random(seed), 1);  // Ciepły
        ensureBiomePresence(new Random(seed), -1); // Zimny
    }

    private void generateChunkStates() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                chunkStates[i][j] = (i == 3 && j == 3) ? 'O' : 'N';
            }
        }
    }

    private void generateBiomeMap() {
        Random random = new Random(seed);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                List<BiomeDefinition> options = getAllowedBiomes(i, j);
                if (options.isEmpty()) options.add(new BiomeDefinition("plains", 0));
                BiomeDefinition chosen = options.get(random.nextInt(options.size()));
                biomeMap[i][j] = chosen;
                Bukkit.getLogger().info(String.format("[LandBound] Biom (%d,%d): %s", i, j, chosen.name()));
            }
        }
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
                    int temp = biomeMap[ni][nj].temperature();
                    if (temp == 1) hasWarmNeighbor = true;
                    if (temp == -1) hasColdNeighbor = true;
                }
            }
        }

        for (BiomeDefinition biome : availableBiomes) {
            if (hasWarmNeighbor && biome.temperature() == -1) continue;
            if (hasColdNeighbor && biome.temperature() == 1) continue;
            candidates.add(biome);
        }
        return candidates;
    }

    private void ensureBiomePresence(Random random, int requiredTemp) {
        boolean found = false;
        for (int i = 0; i < GRID_SIZE && !found; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (biomeMap[i][j].temperature() == requiredTemp) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            for (int trial = 0; trial < 100; trial++) {
                int i = random.nextInt(GRID_SIZE);
                int j = random.nextInt(GRID_SIZE);
                List<BiomeDefinition> candidates = getAllowedBiomes(i, j);
                for (BiomeDefinition biome : candidates) {
                    if (biome.temperature() == requiredTemp) {
                        biomeMap[i][j] = biome;
                        Bukkit.getLogger().info("[LandBound] Dodano brakujący biom " + biome.name());
                        return;
                    }
                }
            }
        }
    }

    public void saveToConfig() {
        List<String> chunkRows = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < GRID_SIZE; j++) {
                row.append(chunkStates[i][j]);
                if (j < GRID_SIZE - 1) row.append(",");
            }
            chunkRows.add(row.toString());
        }
        config.set("chunks", chunkRows);

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int index = i * GRID_SIZE + j;
                BiomeDefinition biome = biomeMap[i][j];
                config.set("biomes." + index, biome != null ? biome.name() : "UNKNOWN");
            }
        }

        try {
            config.save(islandFile);
            Bukkit.getLogger().info("[LandBound] Zapisano plik wyspy: " + islandFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadWorldState() {
        if (!islandFile.exists()) return;

        List<String> rows = config.getStringList("chunks");
        for (int i = 0; i < GRID_SIZE; i++) {
            String[] states = rows.get(i).split(",");
            for (int j = 0; j < GRID_SIZE; j++) {
                chunkStates[i][j] = states[j].charAt(0);
            }
        }

        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            String name = config.getString("biomes." + i, "plains");
            biomeMap[i / GRID_SIZE][i % GRID_SIZE] = Arrays.stream(availableBiomes)
                    .filter(b -> b.name().equals(name))
                    .findFirst()
                    .orElse(new BiomeDefinition("plains", 0));
        }

        Bukkit.getLogger().info("[LandBound] Wczytano istniejący stan świata gracza.");
    }

}
