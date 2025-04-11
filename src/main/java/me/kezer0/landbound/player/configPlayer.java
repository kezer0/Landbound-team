package me.kezer0.landbound.player;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class configPlayer implements Listener {

    private final UUID uuid;
    private final File playerFolder;
    public static File playerFile;
    public static File islandFile;

    private FileConfiguration playerConfig;
    private FileConfiguration islandConfig;

    public configPlayer(UUID uuid) {
        this.uuid = uuid;
        File pluginFolder = new File(Bukkit.getPluginsFolder(), "LandBound");
        File playersFolder = new File(pluginFolder, "players");
        this.playerFolder = new File(playersFolder, uuid.toString());

        this.playerFile = new File(playerFolder, "player.yml");
        this.islandFile = new File(playerFolder, "island.yml");

        setup();
    }

    private void setup() {
        if (!playerFolder.exists()) {
            playerFolder.mkdirs();
        }
        try {
            if (!playerFile.exists()) {
                playerFile.createNewFile();
            }

            if (!islandFile.exists()) {
                islandFile.createNewFile();
                islandConfig = YamlConfiguration.loadConfiguration(islandFile);
                StringBuilder chunkBuilder = new StringBuilder();
                int gridSize = 7;
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (i == 3 && j == 3) {
                            chunkBuilder.append("O");
                        } else {
                            chunkBuilder.append("N");
                        }
                        if (j < gridSize - 1) {
                            chunkBuilder.append(",");
                        }
                    }
                    if (i < gridSize - 1) {
                        chunkBuilder.append("\n");
                    }
                }
                islandConfig.set("chunks", chunkBuilder.toString());
                islandConfig.set("bioms.24", "plains");
                islandConfig.save(islandFile);
            }

            playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            islandConfig = YamlConfiguration.loadConfiguration(islandFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getPlayerConfig() {
        return playerConfig;
    }

    public FileConfiguration getIslandConfig() {
        return islandConfig;
    }

    public void saveConfigs() {
        try {
            playerConfig.save(playerFile);
            islandConfig.save(islandFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
