package me.kezer0.landbound;

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
import java.util.Map;
import java.util.UUID;

public class SwordModeGUI implements Listener, CommandExecutor {

    private static final Map<UUID, String> playerModes = new HashMap<>(); // Przechowuje tryb gracza

   
    private Inventory createModeSelectionGUI() {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE + "Wybierz Tryb");

        // Item dla trybu Strateg
        ItemStack strategItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta strategMeta = strategItem.getItemMeta();
        if (strategMeta != null) {
            strategMeta.setDisplayName(ChatColor.YELLOW + "Tryb Strateg");
            strategMeta.setLore(List.of(ChatColor.GRAY + "Zwiększa obrażenia o 20%."));
            strategItem.setItemMeta(strategMeta);
        }

        // Item dla trybu Mistrz Kontroli
        ItemStack controlItem = new ItemStack(Material.SHIELD);
        ItemMeta controlMeta = controlItem.getItemMeta();
        if (controlMeta != null) {
            controlMeta.setDisplayName(ChatColor.YELLOW + "Mistrz Kontroli");
            controlMeta.setLore(List.of(ChatColor.GRAY + "Odporność na efekty statusu przez 10s."));
            controlItem.setItemMeta(controlMeta);
        }

        // Item dla trybu Regeneracja
        ItemStack regenItem = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta regenMeta = regenItem.getItemMeta();
        if (regenMeta != null) {
            regenMeta.setDisplayName(ChatColor.YELLOW + "Tryb Regeneracji");
            regenMeta.setLore(List.of(ChatColor.GRAY + "Regeneracja 1 przez 5s."));
            regenItem.setItemMeta(regenMeta);
        }

        // Item dla trybu Moc Cienia
        ItemStack shadowItem = new ItemStack(Material.ENDER_PEARL);
        ItemMeta shadowMeta = shadowItem.getItemMeta();
        if (shadowMeta != null) {
            shadowMeta.setDisplayName(ChatColor.YELLOW + "Moc Cienia");
            shadowMeta.setLore(List.of(ChatColor.GRAY + "+15% szansy na krytyczne ciosy przez 10s."));
            shadowItem.setItemMeta(shadowMeta);
        }

        
        gui.setItem(13, strategItem); // Slot 1: Tryb Strateg
        gui.setItem(21, controlItem); // Slot 3: Tryb Mistrz Kontroli
        gui.setItem(29, regenItem);   // Slot 5: Tryb Regeneracji
        gui.setItem(37, shadowItem); // Slot 7: Tryb Moc Cienia

        return gui;
    }

    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Tylko gracze mogą używać tej komendy!");
            return true;
        }

        // Otwórz GUI
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

    
    public static String getPlayerMode(Player player) {
        return playerModes.getOrDefault(player.getUniqueId(), "Strateg");
    }
}