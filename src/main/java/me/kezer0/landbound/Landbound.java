package me.kezer0.landbound;

import me.kezer0.landbound.commands.hubCommand;
import me.kezer0.landbound.commands.landCommand;
import me.kezer0.landbound.land.configLand;
import me.kezer0.landbound.player.configPlayer;
import me.kezer0.landbound.utils.boundaryListener;
import me.kezer0.landbound.utils.worldCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public final class Landbound extends JavaPlugin {

    @Override
    public void onEnable() {
        // Rejestracja komend i eventów
        Bukkit.getPluginManager().registerEvents(new configLand(), this);
        Bukkit.getPluginManager().registerEvents(new boundaryListener(), this);
        Bukkit.getPluginManager().registerEvents(new configPlayer(UUID.randomUUID()), this);
        Objects.requireNonNull(getCommand("land")).setExecutor(new landCommand(this));
        Objects.requireNonNull(getCommand("hub")).setExecutor(new hubCommand());

        // Harmonogram automatycznego zapisu configów co 20 minut
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                configPlayer cp = new configPlayer(player.getUniqueId());
                cp.saveConfigs();
                getLogger().info("Saved config for " + player.getName());
            });
        }, 24000L, 24000L);

        getLogger().info(ChatColor.GREEN + "[LandBound] Loaded");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            configPlayer cp = new configPlayer(player.getUniqueId());
            cp.saveConfigs();
        });
    }
}
