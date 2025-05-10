package me.kezer0.landbound.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeGUI implements Listener, CommandExecutor {

   
    private final Map<UUID, Inventory> tradeInventories = new HashMap<>();

    
    private Inventory createTradeInventory(Player player1, Player player2) {
        String title = ChatColor.DARK_GREEN + "Wymiana: " + player1.getName() + " i " + player2.getName();
        Inventory tradeInventory = Bukkit.createInventory(null, 54, title);

        // Pasek separatora (np. dla akceptacji/odrzucenia wymiany)
        ItemStack separator = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 9; i < 18; i++) {
            tradeInventory.setItem(i, separator);
        }

       
        ItemStack acceptItem = new ItemStack(Material.GREEN_WOOL);
        tradeInventory.setItem(45, acceptItem); 
        tradeInventory.setItem(53, acceptItem); 

        // Sloty anulowania dla obu graczy
        ItemStack cancelItem = new ItemStack(Material.RED_WOOL);
        tradeInventory.setItem(46, cancelItem); 
        tradeInventory.setItem(52, cancelItem);

        return tradeInventory;
    }

    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Tylko gracze mogą używać tej komendy!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Użycie: /wymiana <gracz>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Gracz " + args[0] + " nie jest online!");
            return true;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Nie możesz wymieniać się z samym sobą!");
            return true;
        }

        // Tworzymy GUI wymiany
        Inventory tradeInventory = createTradeInventory(player, target);

        // Przechowujemy GUI dla graczy
        tradeInventories.put(player.getUniqueId(), tradeInventory);
        tradeInventories.put(target.getUniqueId(), tradeInventory);

        // Otwórz GUI dla obu graczy
        player.openInventory(tradeInventory);
        target.openInventory(tradeInventory);

        player.sendMessage(ChatColor.GREEN + "Rozpoczęto wymianę z " + target.getName() + "!");
        target.sendMessage(ChatColor.GREEN + player.getName() + " zaprosił Cię do wymiany!");

        return true;
    }

    /**
     * Obsługuje kliknięcia w GUI wymiany.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        Inventory inventory = event.getInventory();

        // Sprawdzamy, czy GUI jest GUI wymiany
        if (tradeInventories.containsValue(inventory)) {
            int slot = event.getRawSlot();

            // Blokujemy klikanie w separator i przyciski akceptacji/anulowania
            if (slot >= 9 && slot < 18 || slot == 45 || slot == 46 || slot == 52 || slot == 53) {
                event.setCancelled(true);

                // Obsługa akceptacji/anulowania wymiany
                if (slot == 45 || slot == 53) {
                    player.sendMessage(ChatColor.GREEN + "Zaakceptowałeś wymianę!");
                } else if (slot == 46 || slot == 52) {
                    player.sendMessage(ChatColor.RED + "Anulowałeś wymianę!");
                    closeTrade(inventory);
                }
            }
        }
    }

    /**
     * Obsługuje zamknięcie GUI wymiany.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (tradeInventories.containsValue(inventory)) {
            closeTrade(inventory);
        }
    }

    
    private void closeTrade(Inventory inventory) {
        for (UUID uuid : tradeInventories.keySet()) {
            if (tradeInventories.get(uuid).equals(inventory)) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.getOpenInventory().getTopInventory().equals(inventory)) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Wymiana została anulowana.");
                }
            }
        }

        tradeInventories.values().removeIf(inv -> inv.equals(inventory));
    }
}