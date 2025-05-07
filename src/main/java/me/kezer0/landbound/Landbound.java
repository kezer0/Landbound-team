package me.kezer0.landbound;

import me.kezer0.landbound.land.blocks.blockDataListener;
import me.kezer0.landbound.land.blocks.blockDataSaver;
import me.kezer0.landbound.commands.debugCommands.debugCommand;
import me.kezer0.landbound.commands.debugCommands.testItem;
import me.kezer0.landbound.commands.hub.hubCommand;
import me.kezer0.landbound.commands.land.landCommand;
import me.kezer0.landbound.database.databaseManager;
import me.kezer0.landbound.database.itemDatabaseManager;
import me.kezer0.landbound.land.entity.entityDataListener;
import me.kezer0.landbound.land.entity.entityDataSaver;
import me.kezer0.landbound.items.customItems;
import me.kezer0.landbound.land.config.configLand;
import me.kezer0.landbound.land.generation.generateEnvironment;
import me.kezer0.landbound.player.playerDataListener;
import me.kezer0.landbound.utils.boundaryListener;
import me.kezer0.landbound.land.generation.worldDataGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class Landbound extends JavaPlugin {

    private static Landbound instance;

    @Override
    public void onEnable() {
        instance = this;

        databaseManager.initDatabase();
        itemDatabaseManager.createTable();
        itemDatabaseManager.loadAllItems();

        Bukkit.getPluginManager().registerEvents(new configLand(), this);
        Bukkit.getPluginManager().registerEvents(new boundaryListener(), this);
        Bukkit.getPluginManager().registerEvents(new playerDataListener(), this);
        Bukkit.getPluginManager().registerEvents(new blockDataListener(), this);
        Bukkit.getPluginManager().registerEvents(new entityDataListener(), this);

        Objects.requireNonNull(getCommand("land")).setExecutor(new landCommand(this));
        Objects.requireNonNull(getCommand("hub")).setExecutor(new hubCommand());
        Objects.requireNonNull(getCommand("debugitem")).setExecutor(new debugCommand());
        Objects.requireNonNull(getCommand("testitem")).setExecutor(new testItem());

        customItems.registerAll();

        File playersRootFolder = playerDataListener.getPlayersRootFolder();
        Bukkit.getScheduler().runTaskTimer(this, () -> {

            Bukkit.getOnlinePlayers().forEach(player -> {

                File islandFile = new File(playersRootFolder, player.getUniqueId() + "/island.yml");
                worldDataGenerator generator = new worldDataGenerator(player, islandFile);
                generator.saveToConfig();
                getLogger().info("Saved config for " + player.getName());

            });

            blockDataSaver.flushAll();
            entityDataSaver.flushBufferToDisk();
            getLogger().info("Flushed block data buffer to disk.");

        }, 24000L, 24000L);

        getLogger().info(ChatColor.GREEN + "[LandBound] Loaded");
    }
    @Override
    public void onDisable() {
        instance = null;

        File playersRootFolder = playerDataListener.getPlayersRootFolder();
        Bukkit.getOnlinePlayers().forEach(player -> {

            File islandFile = new File(playersRootFolder, player.getUniqueId() + "/island.yml");
            worldDataGenerator generator = new worldDataGenerator(player, islandFile);
            generator.saveToConfig();
            blockDataSaver.flushAll();
            entityDataSaver.flushBufferToDisk();
            playerDataListener.unloadAndDeleteWorld(player);
            getLogger().info("Saved config for " + player.getName());

        });

        databaseManager.close();
        getLogger().info(ChatColor.RED + "[LandBound] Disabled");
    }

    public static Landbound getInstance() {
        return instance;
    }
}
