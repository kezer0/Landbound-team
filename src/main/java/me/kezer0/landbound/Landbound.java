package me.kezer0.landbound;

import me.kezer0.landbound.commands.summonEntity;
import me.kezer0.landbound.land.blocks.blockSaver;
import me.kezer0.landbound.commands.debugCommands.debugCommand;
import me.kezer0.landbound.commands.debugCommands.testItem;
import me.kezer0.landbound.commands.hub.hubCommand;
import me.kezer0.landbound.commands.land.landCommand;
import me.kezer0.landbound.land.database.databaseManager;
import me.kezer0.landbound.land.database.itemDatabase;
import me.kezer0.landbound.land.entity.entitySaver;
import me.kezer0.landbound.items.customItems;
import me.kezer0.landbound.land.generation.generateEnvironment;
import me.kezer0.landbound.land.generation.worldCreator;
import me.kezer0.landbound.player.playerDataManager;
import me.kezer0.landbound.land.generation.worldDataGenerator;
import me.kezer0.landbound.player.playerListener;
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
        itemDatabase.loadAllItems();

        Bukkit.getPluginManager().registerEvents(new generateEnvironment(this), this);
        Bukkit.getPluginManager().registerEvents(new playerListener(),this);

        Objects.requireNonNull(getCommand("land")).setExecutor(new landCommand(this));
        Objects.requireNonNull(getCommand("hub")).setExecutor(new hubCommand());
        Objects.requireNonNull(getCommand("debugitem")).setExecutor(new debugCommand());
        Objects.requireNonNull(getCommand("testitem")).setExecutor(new testItem());
        Objects.requireNonNull(getCommand("entity")).setExecutor(new summonEntity());

        customItems.registerAll();

        File playersRootFolder = playerDataManager.getPlayersRootFolder();
        Bukkit.getScheduler().runTaskTimer(this, () -> {

            Bukkit.getOnlinePlayers().forEach(player -> {

                File islandFile = new File(playersRootFolder, player.getUniqueId() + "/island.yml");
                worldDataGenerator generator = new worldDataGenerator(player, islandFile);
                generator.saveToConfig();
                playerDataManager.savePlayer(player);
                playerDataManager.initPlayer(player);
                getLogger().info("Saved config for " + player.getName());

            });

            blockSaver.flushAll();
            entitySaver.flushBufferToDisk();
            getLogger().info("Flushed block data buffer to disk.");

        }, 24000L, 24000L);

        getLogger().info(ChatColor.GREEN + "[LandBound] Loaded");
    }
    @Override
    public void onDisable() {
        instance = null;

        File playersRootFolder = playerDataManager.getPlayersRootFolder();
        Bukkit.getOnlinePlayers().forEach(player -> {

            File islandFile = new File(playersRootFolder, player.getUniqueId() + "/island.yml");
            worldDataGenerator generator = new worldDataGenerator(player, islandFile);
            generator.saveToConfig();
            blockSaver.flushAll();
            entitySaver.flushBufferToDisk();
            playerDataManager.savePlayer(player);
            worldCreator.unloadAndDeleteWorld(player);
            getLogger().info("Saved config for " + player.getName());

        });

        databaseManager.close();
        getLogger().info(ChatColor.RED + "[LandBound] Disabled");
    }

    public static Landbound getInstance() {
        return instance;
    }
}
