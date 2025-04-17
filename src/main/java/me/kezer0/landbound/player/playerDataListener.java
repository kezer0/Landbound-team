package me.kezer0.landbound.player;

import me.kezer0.landbound.blocks.blockDataSaver;
import me.kezer0.landbound.land.generation.worldCreator;
import me.kezer0.landbound.land.generation.worldDataGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class playerDataListener implements Listener {

    public static final File playersRootFolder = new File(Bukkit.getPluginsFolder(), "LandBound/players");

    static {
        if (!playersRootFolder.exists()) {
            playersRootFolder.mkdirs();
        }
    }
    public static File getPlayersRootFolder() {
        return playersRootFolder;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        File playerFolder = new File(playersRootFolder, uuid.toString());
        File playerFile = new File(playerFolder, "player.yml");
        File islandFile = new File(playerFolder, "island.yml");

        if (!playerFolder.exists()) {
            playerFolder.mkdirs();
        }

        try {
            if (!playerFile.exists()) playerFile.createNewFile();
            if (!islandFile.exists()) islandFile.createNewFile();
        } catch (IOException e) {
            Bukkit.getLogger().severe("[LandBound] Błąd przy tworzeniu plików danych gracza: " + e.getMessage());
            e.printStackTrace();
        }

        worldCreator.createIslandWorld(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        File islandFile = new File(playersRootFolder, uuid + "/island.yml");
        worldDataGenerator generator = new worldDataGenerator(player, islandFile);

        generator.saveToConfig();
        blockDataSaver.flushBufferToDisk(uuid);
        unloadAndDeleteWorld(player);
    }

    public static void unloadAndDeleteWorld(Player player) {
        World world = Bukkit.getWorld(player.getUniqueId().toString());

        if (world == null) return;

        // Teleportujemy wszystkich graczy do domyślnego świata
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
            e.printStackTrace();
        }
    }

    private static void deleteWorldFolder(File folder) throws IOException {
        if (!folder.exists()) return;

        File[] files = folder.listFiles();
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

        if (!folder.delete()) {
            throw new IOException("Nie można usunąć folderu: " + folder.getAbsolutePath());
        }
    }
}
