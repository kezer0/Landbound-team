package me.kezer0.landbound.blocks;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.kezer0.landbound.Landbound;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
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
import org.bukkit.plugin.java.JavaPlugin;

import static me.kezer0.landbound.blocks.blockDataSaver.updateBlock;

public class blockDataListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location loc = event.getBlock().getLocation();
        if (!(loc.getWorld().getName().equals(event.getPlayer().getUniqueId().toString()))) return;

        if (loc.getY() > 63) {
            blockDataSaver.saveBlock(event.getBlock(), event.getPlayer());
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDestroy(BlockDestroyEvent e) {
        Location loc = e.getBlock().getLocation();
        if (loc.getWorld().getName().equals("world") || loc.getWorld().getName().equals("world_nether") || loc.getWorld().getName().equals("world_end")) return;
        if (loc.getY() <= 63) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        updateBlock(event.getBlock());
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Bukkit.getLogger().warning("ZMIANA");
        blockDataSaver.saveBlock(event.getBlock(), event.getPlayer());
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        Bukkit.getLogger().warning("INTERAKCJA");
        Block block = e.getClickedBlock();
        if (block != null && block.getState() instanceof Sign sign) {
            Material handItem = e.getItem() != null ? e.getItem().getType() : Material.AIR;
            if (handItem.name().endsWith("_DYE") || handItem == Material.GLOW_INK_SAC || handItem == Material.INK_SAC) {
                Bukkit.getScheduler().runTaskLater(Landbound.getInstance(), () -> {
                    blockDataSaver.saveBlock(block, e.getPlayer());
                }, 1L);
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        if (event.getInventory().getHolder() instanceof BlockInventoryHolder holder) {
            Block block = holder.getBlock();
            blockDataSaver.saveBlock(block, player);
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getLocation().getY() > 63) {
            blockDataSaver.removeBlock(event.getBlock(), event.getPlayer());
        }
    }
}
