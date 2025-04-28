package me.kezer0.landbound;

import me.kezer0.landbound.blocks.blockDataListener;
import me.kezer0.landbound.blocks.blockDataSaver;
import me.kezer0.landbound.commands.debugCommands.debugCommand;
import me.kezer0.landbound.commands.debugCommands.testItem;
import me.kezer0.landbound.commands.hub.hubCommand;
import me.kezer0.landbound.commands.land.landCommand;
import me.kezer0.landbound.database.databaseManager;
import me.kezer0.landbound.database.itemDatabaseManager;
import me.kezer0.landbound.items.customItems;
import me.kezer0.landbound.land.config.configLand;
import me.kezer0.landbound.player.playerDataListener;
import me.kezer0.landbound.utils.boundaryListener;
import me.kezer0.landbound.land.generation.worldDataGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import org.bukkit.inventory.ItemStack;

public final class Landbound extends JavaPlugin {

    private static Landbound instance;

    @Override
    public void onEnable() {
        instance = this;
        databaseManager.initDatabase();
        itemDatabaseManager.createTable();//
        itemDatabaseManager.loadAllItems();

        // Eventy
        Bukkit.getPluginManager().registerEvents(new configLand(), this);
        Bukkit.getPluginManager().registerEvents(new boundaryListener(), this);
        Bukkit.getPluginManager().registerEvents(new playerDataListener(), this);
        Bukkit.getPluginManager().registerEvents(new blockDataListener(), this);

        // Komendy
        Objects.requireNonNull(getCommand("land")).setExecutor(new landCommand(this));
        Objects.requireNonNull(getCommand("hub")).setExecutor(new hubCommand());
        Objects.requireNonNull(getCommand("debugitem")).setExecutor(new debugCommand());
        Objects.requireNonNull(getCommand("testitem")).setExecutor(new testItem());

        // Rejestracja customowych itemów
        customItems.registerAll();
        Map<String, ItemStack> customItemsFromDb = itemDatabaseManager.loadAllItems();

        // Autosejwy co 20 minut
        File playersRootFolder = playerDataListener.getPlayersRootFolder();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                File islandFile = new File(playersRootFolder, player.getUniqueId() + "/island.yml");
                worldDataGenerator generator = new worldDataGenerator(player, islandFile);
                generator.saveToConfig();
                getLogger().info("Saved config for " + player.getName());
            });
            blockDataSaver.flushAll();
            getLogger().info("Flushed block data buffer to disk.");
        }, 24000L, 24000L);  // 20 minut (1200 ticków = 1 minuta)

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
            playerDataListener.unloadAndDeleteWorld(player); // <-- Twoja nowa metoda!
            getLogger().info("Saved config for " + player.getName());
        });

        databaseManager.close();
        getLogger().info(ChatColor.RED + "[LandBound] Disabled");
    }
    public static Landbound getInstance() {
        return instance;
    }
}
