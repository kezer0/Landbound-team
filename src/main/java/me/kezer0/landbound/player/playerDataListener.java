package me.kezer0.landbound.player;

import me.kezer0.landbound.land.blocks.blockDataSaver;
import me.kezer0.landbound.database.databaseManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class playerDataListener implements Listener {

    public static File getPlayersRootFolder() {

        File folder = new File(Bukkit.getPluginManager().getPlugin("Landbound").getDataFolder(), "players");
        if (!folder.exists()) {

            folder.mkdirs();
        }
        return folder;
    }

    public static void unloadAndDeleteWorld(Player player) {

        World world = Bukkit.getWorld(player.getUniqueId().toString());

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
    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e){

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        blockDataSaver.flushBufferToDisk(uuid);
        unloadAndDeleteWorld(player);
        databaseManager.close();
    }
}