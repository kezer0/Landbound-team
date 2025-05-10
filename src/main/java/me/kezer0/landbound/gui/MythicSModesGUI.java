package me.kezer0.landbound.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MythicSModesGUI implements Listener, CommandExecutor {

    private static final Map<UUID, String> playerModes = new HashMap<>(); // Przechowuje tryb gracza

   
    private Inventory createModeSelectionGUI() {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE + "Wybierz Tryb");

        ItemStack strategItem = createGuiItem(Material.IRON_SWORD, ChatColor.YELLOW + "Tryb Strateg", "Zwiększa obrażenia o 20%.");

        ItemStack controlItem = createGuiItem(Material.SHIELD, ChatColor.YELLOW + "Mistrz Kontroli", "Odporność na efekty statusu przez 10s.");

        ItemStack regenItem = createGuiItem(Material.GOLDEN_APPLE, ChatColor.YELLOW + "Tryb Regeneracji", "Regeneracja 1 przez 5s.");

        ItemStack shadowItem = createGuiItem(Material.ENDER_PEARL, ChatColor.YELLOW + "Moc Cienia", "+15% szansy na krytyczne ciosy przez 10s.");

        gui.setItem(13, strategItem); 
        gui.setItem(22, controlItem); 
        gui.setItem(30, regenItem);   
        gui.setItem(38, shadowItem); 

        return gui;
    }

    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Tylko gracze mogą używać tej komendy!");
            return true;
        }

       
        Inventory gui = createModeSelectionGUI();
        player.openInventory(gui);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Wybierz Tryb")) {
            event.setCancelled(true); // Zablokuj przesuwanie przedmiotów

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName() != null) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                if (displayName.equals(ChatColor.YELLOW + "Tryb Strateg")) {
                    playerModes.put(player.getUniqueId(), "Strateg");
                    player.sendMessage(ChatColor.GREEN + "Wybrano tryb: " + ChatColor.YELLOW + "Strateg");
                } else if (displayName.equals(ChatColor.YELLOW + "Mistrz Kontroli")) {
                    playerModes.put(player.getUniqueId(), "Mistrz Kontroli");
                    player.sendMessage(ChatColor.GREEN + "Wybrano tryb: " + ChatColor.YELLOW + "Mistrz Kontroli");
                } else if (displayName.equals(ChatColor.YELLOW + "Tryb Regeneracji")) {
                    playerModes.put(player.getUniqueId(), "Regeneracja");
                    player.sendMessage(ChatColor.GREEN + "Wybrano tryb: " + ChatColor.YELLOW + "Regeneracja");
                } else if (displayName.equals(ChatColor.YELLOW + "Moc Cienia")) {
                    playerModes.put(player.getUniqueId(), "Moc Cienia");
                    player.sendMessage(ChatColor.GREEN + "Wybrano tryb: " + ChatColor.YELLOW + "Moc Cienia");
                }

                player.closeInventory();
            }
        }
    }

    
    private ItemStack createGuiItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(ChatColor.GRAY + lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    
    public static String getPlayerMode(Player player) {
        return playerModes.getOrDefault(player.getUniqueId(), "Strateg");
    }
}