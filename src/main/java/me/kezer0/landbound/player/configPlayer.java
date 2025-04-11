package me.kezer0.landbound.player;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class configPlayer implements Listener {

    private File playerFolder;
    public static File playerFile;
    public static File islandFile;
    public static File playersFolders;

    private FileConfiguration playerConfig;
    private FileConfiguration islandConfig;

    static {
        File pluginFolder = new File(Bukkit.getPluginsFolder(), "LandBound");
        playersFolders = new File(pluginFolder, "players");
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        this.playerFolder = new File(playersFolders, uuid.toString());
        playerFile = new File(playerFolder, "player.yml");
        islandFile = new File(playerFolder, "island.yml");

        if (playerFolder.exists()) return;

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

                int gridSize = 7;
                List<String> chunkRows = new ArrayList<>();

                for (int i = 0; i < gridSize; i++) {
                    StringBuilder row = new StringBuilder();
                    for (int j = 0; j < gridSize; j++) {
                        row.append((i == 4 && j == 4) ? "O" : "N");
                        if (j < gridSize - 1) row.append(",");
                    }
                    chunkRows.add(row.toString());
                }

                islandConfig.set("chunks", chunkRows);
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
