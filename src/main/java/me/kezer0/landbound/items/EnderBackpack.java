package me.kezer0.landbound;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EnderBackpack implements Listener {

    /**
     * Tworzy niestandardowy przedmiot "Plecak Endera".
     * @return ItemStack reprezentujący plecak Endera.
     */
    public static ItemStack createEnderBackpack() {
        ItemStack chest = new ItemStack(Material.ENDER_CHEST); // Użycie Ender Chest jako materiału
        ItemMeta meta = chest.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Plecak Endera");
            meta.setLore(List.of(
                ChatColor.DARK_PURPLE + "EPIC",
                ChatColor.GRAY + "Przenośny ender chest",
                ChatColor.GREEN + "Funkcja specjalna: Otwiera prywatny magazyn"
            ));
            chest.setItemMeta(meta);
        }
        return chest;
    }

    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem(); // Pobierz przedmiot, którym gracz kliknął
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String displayName = item.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.DARK_AQUA + "Plecak Endera")) {
                event.setCancelled(true); // Anuluj domyślne działanie przedmiotu
                
                // Otwórz skrzynkę Endera z własnym tytułem
                Inventory enderBackpack = Bukkit.createInventory(event.getPlayer(), 27, ChatColor.DARK_AQUA + "Plecak Endera");
                enderBackpack.setContents(event.getPlayer().getEnderChest().getContents()); // Skopiuj zawartość skrzyni Endera
                event.getPlayer().openInventory(enderBackpack);
            }
        }
    }
}