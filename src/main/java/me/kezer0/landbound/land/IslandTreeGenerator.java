package me.kezer0.landbound.land;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.TreeType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class IslandTreeGenerator implements Listener {

    private final JavaPlugin plugin;

   
    public IslandTreeGenerator(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();

        
        if (!world.getEnvironment().equals(World.Environment.NORMAL)) {
            plugin.getLogger().warning("Drzewa mogą być generowane tylko w świecie Overworld.");
            return;
        }

        
        int centerX = 0;
        int centerZ = 0;
        int centerY = world.getHighestBlockYAt(centerX, centerZ); 

        // Ustalenie lokalizacji drzewa
        Location treeLocation = new Location(world, centerX, centerY + 1, centerZ);

        // Sprawdzenie, czy lokalizacja jest wolna
        if (world.getBlockAt(treeLocation).getType() != Material.AIR) {
            plugin.getLogger().warning("Lokalizacja drzewa nie jest pusta. Nie można wygenerować drzewa.");
            return;
        }

        // Wygeneruj drzewo na środku wyspy
        boolean success = world.generateTree(treeLocation, TreeType.TREE);

        if (success) {
            plugin.getLogger().info("Drzewo zostało wygenerowane na środku wyspy!");
        } else {
            plugin.getLogger().warning("Nie udało się wygenerować drzewa na środku wyspy.");
        }
    }
}