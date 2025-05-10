package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class MagicAxe implements Listener {

 
    public static ItemStack createMagicAxe() {
        ItemStack magicaxe = new ItemStack(Material.DIAMOND_AXE); 
        ItemMeta meta = magicaxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.AQUA + "MAGICZNA SIEKIERKA");
            meta.setLore(List.of(
                ChatColor.DARK_PURPLE + "EPIC",
                ChatColor.GRAY + "To nie jest zwykła siekierka...",
                ChatColor.GREEN + "Funkcja specjalna",
                ChatColor.GOLD + " - Niszczy całe drzewo naraz!"
            ));

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            meta.addEnchant(Enchantment.UNBREAKING, 3, true); 
            meta.addEnchant(Enchantment.EFFICIENCY, 3, true); 
            magicaxe.setItemMeta(meta);
        }
        return magicaxe;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();


        if (itemInHand != null && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()) {
            String displayName = itemInHand.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.AQUA + "Magiczna Siekierka")) {
                Block block = event.getBlock();

                
                if (isLogBlock(block.getType())) {
                    event.setCancelled(true); 
                    destroyTree(block); 
                }
            }
        }
    }

    
    private boolean isLogBlock(Material material) {
        return material == Material.OAK_LOG || material == Material.BIRCH_LOG || material == Material.SPRUCE_LOG ||
               material == Material.JUNGLE_LOG || material == Material.ACACIA_LOG || material == Material.DARK_OAK_LOG;
    }

 
    private void destroyTree(Block startBlock) {
        Set<Block> visited = new HashSet<>(); 
        Queue<Block> queue = new ArrayDeque<>(); 
        queue.add(startBlock);

        while (!queue.isEmpty()) {
            Block current = queue.poll();


            if (visited.add(current) && isLogBlock(current.getType())) {
                current.breakNaturally(); 
                
                for (Block neighbor : getNeighbors(current)) {
                    if (!visited.contains(neighbor) && isLogBlock(neighbor.getType())) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

   
    private Block[] getNeighbors(Block block) {
        return new Block[] {
            block.getRelative(1, 0, 0),  
            block.getRelative(-1, 0, 0), 
            block.getRelative(0, 1, 0),  
            block.getRelative(0, -1, 0),
            block.getRelative(0, 0, 1),  
            block.getRelative(0, 0, -1)  
        };
    }
}  
