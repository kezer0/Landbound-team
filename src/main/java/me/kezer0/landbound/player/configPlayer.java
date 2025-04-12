package me.kezer0.landbound.player;

import me.kezer0.landbound.utils.worldGenerator;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.FileUtil;

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
    public void unloadWorld(Player player) {
        String worldName = player.getUniqueId().toString();
        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            for (Player p : world.getPlayers()) {
                p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }

            boolean success = Bukkit.unloadWorld(world, false);
            if (!success) {
                Bukkit.getLogger().warning("[LandBound] Nie udało się odładować świata: " + worldName);
                return;
            }

            File worldFolder = world.getWorldFolder();
            try {
                deleteWorldFolder(worldFolder);
                Bukkit.getLogger().info("[LandBound] Usunięto świat gracza: " + worldName);
            } catch (IOException e) {
                Bukkit.getLogger().warning("[LandBound] Błąd przy usuwaniu świata: " + worldName);
                e.printStackTrace();
            }
        }
    }

    public void deleteWorldFolder(File path) throws IOException {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFolder(file);
                    } else {
                        if (!file.delete()) {
                            throw new IOException("Nie można usunąć pliku: " + file.getAbsolutePath());
                        }
                    }
                }
            }
            if (!path.delete()) {
                throw new IOException("Nie można usunąć folderu: " + path.getAbsolutePath());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        worldGenerator generator = new worldGenerator(player);
        generator.saveToConfig();
        unloadWorld(player);
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
                try {
                    islandFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
