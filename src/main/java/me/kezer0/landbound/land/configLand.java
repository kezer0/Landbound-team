package me.kezer0.landbound.land;

import me.kezer0.landbound.utils.CColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.kezer0.landbound.utils.worldGenerator;
import me.kezer0.landbound.utils.worldCreator;
import me.kezer0.landbound.player.configPlayer;

import java.util.*;

import static me.kezer0.landbound.utils.headCreator.getCustomHead;

public class configLand implements Listener {

    public static Map<UUID, Inventory> creatorInventories = new HashMap<>();

    public static void createorInventory(Player player){
        Inventory gui = Bukkit.createInventory(null,54,"Create your land");

        ItemStack gP = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for(int i = 0; i < 54; i++){
            gui.setItem(i,gP);
        }
        List<String> lore = new ArrayList<>();
            lore.add(CColor.GREEN + player.getName() + "'s Lands");
        ItemStack planet = getCustomHead(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTU3N2M0ZGUxZjUxYTcwNzIyMDIzZTg1NmI1NDNjZDU3MGYxZDBlZTZiOWQxNjdiNTkwMjhjZTFiYzkyZTQ1OCJ9fX0=",
                CColor.BLUE + "You'r land",
                lore
        );
        gui.setItem(4,planet);

        creatorInventories.put(player.getUniqueId(), gui);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player player)) return;
        player.sendMessage("dziala");
        UUID uuid = player.getUniqueId();
        Inventory inv = e.getInventory();
        if(!creatorInventories.containsKey(uuid) || !creatorInventories.get(uuid).equals(inv)) return;

        int slot = e.getSlot();

        e.setCancelled(true);
        if(slot == 4){
            player.sendMessage("Dziala");
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getViewers() instanceof Player)) return;

        Player player = (Player) e.getPlayer();
        Inventory inv = e.getInventory();


        if (creatorInventories.containsKey(player.getUniqueId()) && creatorInventories.get(player.getUniqueId()).equals(inv)) {
            player.sendMessage(ChatColor.RED + "Zamknąłeś GUI!");
            creatorInventories.remove(player.getUniqueId());
        }
    }


}
