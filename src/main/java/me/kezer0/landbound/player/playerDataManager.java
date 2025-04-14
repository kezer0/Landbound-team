package me.kezer0.landbound.player;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class playerDataManager {

    private final File playerFile;
    private final File islandFile;
    private final FileConfiguration playerConfig;
    private final FileConfiguration islandConfig;

    public playerDataManager(UUID playerUUID) {
        File playerFolder = new File("plugins/LandBound/players", playerUUID.toString());

        if (!playerFolder.exists()) {
            playerFolder.mkdirs();
        }

        this.playerFile = new File(playerFolder, "player.yml");
        this.islandFile = new File(playerFolder, "island.yml");

        createFileIfNotExists(playerFile);
        createFileIfNotExists(islandFile);

        this.playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        this.islandConfig = YamlConfiguration.loadConfiguration(islandFile);
    }

    private void createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getPlayerConfig() {
        return playerConfig;
    }

    public FileConfiguration getIslandConfig() {
        return islandConfig;
    }

    public void save() {
        try {
            playerConfig.save(playerFile);
            islandConfig.save(islandFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getIslandFile() {
        return islandFile;
    }

    public File getPlayerFile() {
        return playerFile;
    }
}
