package me.kezer0.landbound.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class boundaryListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        World world = player.getWorld();

        // Sprawdzamy czy to jest świat gracza
        if (!world.getName().equalsIgnoreCase(player.getUniqueId().toString())) return;

        // Sprawdzamy czy stoi w wodzie
        Block block = loc.getBlock();
        if (block.getType() != Material.WATER) return;

        // Teleportujemy na najwyższy bezpieczny blok na środku wyspy (0, ?, 0)
        Location center = new Location(world, 0, 0, 0);
        int highestY = world.getHighestBlockYAt(center);
        center.setY(highestY + 1); // 1 ponad najwyższy blok
        center.setPitch(player.getLocation().getPitch());
        center.setYaw(player.getLocation().getYaw());

        player.teleport(center);
        player.sendMessage("Nie możesz wchodzić do wody! Przenoszę Cię na wyspę.");
    }
}
