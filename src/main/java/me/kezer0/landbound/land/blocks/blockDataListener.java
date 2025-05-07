package me.kezer0.landbound.land.blocks;

import me.kezer0.landbound.Landbound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import static me.kezer0.landbound.land.blocks.blockDataSaver.saveBlock;
import static me.kezer0.landbound.land.blocks.blockDataSaver.updateBlock;

public class blockDataListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Location loc = e.getBlock().getLocation();
        if (!(loc.getWorld().getName().equals(e.getPlayer().getUniqueId().toString()))) return;

        if (loc.getY() > 63) {
            saveBlock(e.getBlock(), e.getPlayer());
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getLocation().getY() > 63) {
            blockDataSaver.removeBlock(e.getBlock(), e.getPlayer());
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        updateBlock(e.getBlock());
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        saveBlock(e.getBlock(), e.getPlayer());
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {

        Block block = e.getClickedBlock();

        if (block != null && block.getState() instanceof Sign) {

            Material handItem = e.getItem() != null ? e.getItem().getType() : Material.AIR;

            if (handItem.name().endsWith("_DYE") || handItem == Material.GLOW_INK_SAC || handItem == Material.INK_SAC) {
                Bukkit.getScheduler().runTaskLater(Landbound.getInstance(), () -> {
                    saveBlock(block, e.getPlayer());
                }, 1L);
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;

        Inventory inv = e.getInventory();
        InventoryHolder holder = inv.getHolder();

        if (holder instanceof BlockInventoryHolder chest) {
            Block block = chest.getBlock();
            saveBlock(block, player);
        }
    }


}
