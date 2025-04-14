package me.kezer0.landbound;

import me.kezer0.landbound.commands.hubCommand;
import me.kezer0.landbound.commands.landCommand;
import me.kezer0.landbound.land.configLand;
import me.kezer0.landbound.player.playerDataListener;
import me.kezer0.landbound.utils.boundaryListener;
import me.kezer0.landbound.utils.worldDataGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class Landbound extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new configLand(), this);
        Bukkit.getPluginManager().registerEvents(new boundaryListener(), this);

        File playersRootFolder = playerDataListener.getPlayersRootFolder();

        Bukkit.getPluginManager().registerEvents(new playerDataListener(), this);

        Objects.requireNonNull(getCommand("land")).setExecutor(new landCommand(this));
        Objects.requireNonNull(getCommand("hub")).setExecutor(new hubCommand());

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                File islandFile = new File(playersRootFolder, player.getUniqueId() + "/island.yml");
                worldDataGenerator generator = new worldDataGenerator(player, islandFile);
                generator.saveToConfig();
                getLogger().info("Saved config for " + player.getName());
            });
        }, 24000L, 24000L);  // 20 min

        getLogger().info(ChatColor.GREEN + "[LandBound] Loaded");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            File playersRootFolder = playerDataListener.getPlayersRootFolder();
            File islandFile = new File(playersRootFolder, player.getUniqueId() + "/island.yml");
            worldDataGenerator generator = new worldDataGenerator(player, islandFile);
            generator.saveToConfig();
            playerDataListener.unloadAndDeleteWorld(player);
            getLogger().info("Saved config for " + player.getName());
        });

        getLogger().info(ChatColor.RED + "[LandBound] Disabled");
    }
}
