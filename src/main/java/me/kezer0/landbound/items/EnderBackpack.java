package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemFlag;
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

    public static ItemStack createEnderBackpack() {
        ItemStack chest = new ItemStack(Material.ENDER_CHEST); 
        ItemMeta meta = chest.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "PLECAK KRESU");
            meta.setLore(List.of(
                ChatColor.DARK_PURPLE + "EPIC",
                ChatColor.DARK_AQUA + "Przenośny ender chest",
                ChatColor.GREEN + "Funkcja specjalna: ",
                ChatColor.DARK_AQUA + " Otwiera prywatny magazyn"
            ));

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); 

            chest.setItemMeta(meta);
        }
        return chest;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem(); // Pobierz przedmiot, którym gracz kliknął
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String displayName = item.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "PLECAK KRESU")) {
                event.setCancelled(true); 

                Inventory enderBackpack = Bukkit.createInventory(event.getPlayer(), 27, ChatColor.DARK_PURPLE + "Plecak KRESU");
                enderBackpack.setContents(event.getPlayer().getEnderChest().getContents()); 
                event.getPlayer().openInventory(enderBackpack);
            }
        }
    }
}
