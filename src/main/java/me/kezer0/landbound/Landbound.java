package me.kezer0.landbound;

import me.kezer0.landbound.listeners.*;
import me.kezer0.landbound.land.IslandTreeGenerator;
import me.kezer0.landbound.commands.LandItemsCommand;
import me.kezer0.landbound.gui.TradeGUI;
import me.kezer0.landbound.gui.MythicSModesGUI;
import me.kezer0.landbound.commands.hubCommand;
import me.kezer0.landbound.commands.landCommand;
import me.kezer0.landbound.land.configLand;
import me.kezer0.landbound.player.configPlayer;
import me.kezer0.landbound.utils.boundaryListener;
import me.kezer0.landbound.utils.worldCreator;
import me.kezer0.landbound.items.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public final class Landbound extends JavaPlugin {

    private static Landbound instance;

    public static Landbound getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Rejestracja eventów i listenerów
        Bukkit.getPluginManager().registerEvents(new configLand(), this);
        Bukkit.getPluginManager().registerEvents(new boundaryListener(), this);
        Bukkit.getPluginManager().registerEvents(new configPlayer(UUID.randomUUID()), this);

        // Rejestracja komend
        Objects.requireNonNull(getCommand("land")).setExecutor(new landCommand(this));
        Objects.requireNonNull(getCommand("hub")).setExecutor(new hubCommand());
        Objects.requireNonNull(getCommand("wymiana")).setExecutor(new TradeGUI());
        Objects.requireNonNull(getCommand("zmientryb")).setExecutor(new MythicSModesGUI());
        Objects.requireNonNull(getCommand("dev")).setExecutor(new LandItemsCommand());

        // Rejestracja customowych przedmiotów/listenerów
        Bukkit.getPluginManager().registerEvents(new FrostMaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new EnderBackpackListener(this), this);

        // Auto-zapisywanie danych graczy co 20 minut
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
        // Auto-zapis przy wyłączaniu serwera
        Bukkit.getOnlinePlayers().forEach(player -> {
            configPlayer cp = new configPlayer(player.getUniqueId());
            cp.saveConfigs();
        });
    }
}