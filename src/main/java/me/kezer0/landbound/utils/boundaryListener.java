package me.kezer0.landbound.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class boundaryListener implements Listener {

    private final int minX = -8;
    private final int maxX = 7;
    private final int minZ = -8;
    private final int maxZ = 7;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        World world = player.getWorld();
        if (!player.getWorld().getName().equalsIgnoreCase(player.getUniqueId().toString())) return;
        try {
            int x = loc.getBlockX();
            int z = loc.getBlockZ();
            if (x < minX || x > maxX || z < minZ || z > maxZ) {
                // Teleportujemy gracza na środek chunka (0,65,0)
                Location safeLoc = new Location(world, 0, 65, 0);
                player.teleport(safeLoc);
                player.sendMessage("Nie możesz wychodzić poza Twoją wyspę! Przenoszę na środek.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
