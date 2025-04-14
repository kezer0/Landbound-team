package me.kezer0.landbound.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class boundaryListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        World world = player.getWorld();
        if (!world.getName().equalsIgnoreCase(player.getUniqueId().toString())) return;

        Block block = loc.getBlock();
        if (block.getType() != Material.WATER) return;

        Location center = new Location(world, 8, 0, 8);
        int highestY = world.getHighestBlockYAt(center);
        center.setY(highestY + 1);
        center.setPitch(player.getLocation().getPitch());
        center.setYaw(player.getLocation().getYaw());

        player.teleport(center);
        player.sendMessage("Nie możesz wchodzić do wody! Przenoszę Cię na wyspę.");
    }
    @EventHandler
    public void onWaterSpread(BlockFromToEvent event) {
        World world = event.getBlock().getWorld();
        if(world.getName().equals("world") || world.getName().equals("world_nether") || world.getName().equals("world_end")) return;
        if (event.getBlock().getType() == Material.WATER) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if(e.getMaterial() == Material.WATER){
            if(player.getInventory().getItemInMainHand().getType() == Material.BUCKET){
                player.getInventory().setItemInMainHand(ItemStack.of(Material.WATER_BUCKET));
                e.setCancelled(true);
            }
        }
    }
}
