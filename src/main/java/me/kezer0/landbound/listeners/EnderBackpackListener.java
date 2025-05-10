package me.kezer0.landbound.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderBackpackListener implements Listener {

    private final JavaPlugin plugin;

    public EnderBackpackListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "ender_backpack");

        if (meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            event.setCancelled(true);

            Inventory enderBackpack = Bukkit.createInventory(event.getPlayer(), 27, ChatColor.DARK_PURPLE + "Plecak KRESU");
            enderBackpack.setContents(event.getPlayer().getEnderChest().getContents());
            event.getPlayer().openInventory(enderBackpack);
        }
    }
}