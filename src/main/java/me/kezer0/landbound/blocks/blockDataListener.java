package me.kezer0.landbound.blocks;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class blockDataListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location loc = event.getBlock().getLocation();
        if(loc.getY() > 63){
            blockDataSaver.saveBlock(event.getBlock(), event.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if(loc.getY() > 63) {
            blockDataSaver.removeBlock(event.getBlock(), event.getPlayer());
        }
    }
}
