package me.kezer0.landbound.land.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class boundaryListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location loc = player.getLocation();
        World world = player.getWorld();

        if (!world.getName().startsWith("island_")) return;

        Block block = loc.getBlock();

        if(block.getLocation().getY() > 63) return;
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
    public void onWaterSpread(BlockFromToEvent e) {
        World world = e.getBlock().getWorld();

        if(!(world.getName().startsWith("island_"))) return;
        if (e.getBlock().isLiquid() && e.getBlock().getLocation().getY() < 63) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        Player player = e.getPlayer();
        if (e.getBlockClicked().getType() == Material.WATER) {
            if (player.getInventory().getItemInMainHand().getType() == Material.BUCKET) {

                ItemStack waterBucket = new ItemStack(Material.WATER_BUCKET);
                player.getInventory().setItemInMainHand(waterBucket);
                e.getBlockClicked().setType(Material.WATER);
                e.setCancelled(true);
            }
        }
    }

}
