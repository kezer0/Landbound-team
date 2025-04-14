package me.kezer0.landbound;

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

    /**
     * Tworzy Magiczną Siekierkę.
     * @return ItemStack reprezentujący Magiczną Siekierkę.
     */
    public static ItemStack createMagicAxe() {
        ItemStack magicaxe = new ItemStack(Material.DIAMOND_AXE); 
        ItemMeta meta = magicaxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.AQUA + "Magiczna Siekierka");
            meta.setLore(List.of(
                ChatColor.DARK_PURPLE + "EPIC",
                ChatColor.GRAY + "To nie jest zwykła siekierka...",
                ChatColor.GREEN + "Funkcja specjalna",
                ChatColor.GOLD + "Niszczy całe drzewo naraz!"
            ));
            meta.addEnchant(Enchantment.UNBREAKING, 3, true); 
            meta.addEnchant(Enchantment.EFFICIENCY, 3, true); 
            axe.setItemMeta(meta);
        }
        return magicaxe;
    }

    /**
     * Obsługuje zdarzenie niszczenia bloku. Jeśli gracz używa Magicznej Siekierki,
     * zniszczy wszystkie bloki drzewa powiązane z danym blokiem.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Sprawdź, czy gracz używa Magicznej Siekierki
        if (itemInHand != null && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()) {
            String displayName = itemInHand.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.AQUA + "Magiczna Siekierka")) {
                Block block = event.getBlock();

                // Sprawdź, czy blok to pień drzewa
                if (isLogBlock(block.getType())) {
                    event.setCancelled(true); // Anuluj domyślne niszczenie
                    destroyTree(block); // Zniszcz całe drzewo
                }
            }
        }
    }

    /**
     * Sprawdza, czy dany typ bloku to pień drzewa.
     * @param material Typ bloku
     * @return true, jeśli blok to pień drzewa.
     */
    private boolean isLogBlock(Material material) {
        return material == Material.OAK_LOG || material == Material.BIRCH_LOG || material == Material.SPRUCE_LOG ||
               material == Material.JUNGLE_LOG || material == Material.ACACIA_LOG || material == Material.DARK_OAK_LOG;
    }

    /**
     * Niszczy całe drzewo, zaczynając od danego bloku.
     * @param startBlock Blok początkowy (pień drzewa).
     */
    private void destroyTree(Block startBlock) {
        Set<Block> visited = new HashSet<>(); // Zestaw odwiedzonych bloków
        Queue<Block> queue = new ArrayDeque<>(); // Kolejka do BFS
        queue.add(startBlock);

        while (!queue.isEmpty()) {
            Block current = queue.poll();

            // Jeśli blok nie był jeszcze odwiedzony i jest pniem drzewa
            if (visited.add(current) && isLogBlock(current.getType())) {
                current.breakNaturally(); // Niszczenie bloku w naturalny sposób

                // Dodaj sąsiednie bloki do kolejki
                for (Block neighbor : getNeighbors(current)) {
                    if (!visited.contains(neighbor) && isLogBlock(neighbor.getType())) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    /**
     * Zwraca listę sąsiednich bloków dla danego bloku.
     * @param block Blok, dla którego szukamy sąsiadów.
     * @return Tablica sąsiednich bloków.
     */
    private Block[] getNeighbors(Block block) {
        return new Block[] {
            block.getRelative(1, 0, 0),  // +X
            block.getRelative(-1, 0, 0), // -X
            block.getRelative(0, 1, 0),  // +Y
            block.getRelative(0, -1, 0), // -Y
            block.getRelative(0, 0, 1),  // +Z
            block.getRelative(0, 0, -1)  // -Z
        };
    }
}  
