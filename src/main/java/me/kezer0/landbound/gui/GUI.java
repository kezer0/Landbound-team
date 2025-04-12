package me.kezer0.landbound.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.kezer0.landbound.utils.headCreator.getCustomHead;

public class GUI {

    private static Map<UUID, Inventory> guiInventories = new HashMap<>(); //tworzymy mapę (argumenty <UUID, Inventory> możesz zawsze tak dawać) w ktorej będziemy trzymać wszystkich graczy którzy mają otware GUI

    //tworzymy metode tworzenia i otwierania GUI
    public static void createorInventory(Player player ){ //bierzemy w agrument gracza
        Inventory gui = Bukkit.createInventory(null,54,"interfejs"); //tak tworzymy GUI żeby serwer o nim wiedział
        //w .createInventory(właściciel tu zawsze dajemy null, size tu dajemy ilość slotów zazwyczaj 54 lub 27, tytuł pojawja się on na samej górze GUI może być dowolny
        ItemStack gray_pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE); //tworzenie przedmiotu
        for(int i = 0; i < 54; i++){ //tworzymy pętle która będzie przechodzić po koleji każdy slot w GUI i wypełniać go przedmiotem
            gui.setItem(i,gray_pane); // np i = 2 więc w slocie 3 (bo liczymy od zera) będzie przedmiot
            
        }
       ItemStack diamond = new ItemStack(Material.DIAMOND); // Tworzenie przedmiotu - diament
gui.setItem(22, diamond); // Umieszczenie diamentu w slocie 22
        guiInventories.put(player.getUniqueId(), gui); // tutaj funkcja .put oznacza włożyć jakiegoś gracza do listy (argumenty są takie jak podaliśmy czyli <UUID, Inventory> czyli podajemy uuid gracza ktory otworzyl GUI i podajemy jakie GUI otworzył
        player.openInventory(gui); // funkcja która po utworzeniu GUI pokazuje je graczowi
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e){ //bierzemy InventoryCLickEvent w argument (przypisujemy go jako e)
        if(!(e.getWhoClicked() instanceof Player player)) return; //sprawdzamy czy byt wykonujący event jest graczem jeśli nie to wychodzimy z eventu (i przyokazji przypisujemy gracza)
        UUID uuid = player.getUniqueId(); //przypisujemy warość UUID gracza
        Inventory inv = e.getInventory(); // przypisujemy jakie inventory jest otwarte
        if(!guiInventories.containsKey(uuid) || !guiInventories.get(uuid).equals(inv)) return; // sprawdzamy czy inventory które gracz otwarł jest gracza i czy jest to poprawne inventory (żeby nie blokowac innych inventory)

        int slot = e.getSlot();
        e.setCancelled(true);                   // ustawiamy żeby wyłączyć działanie czyli po prostu nie da się wyjąć przedmiotu z inventroy (musi być zawsze taki jest cel tego eventu)

                                                //  jakąś funkcje można tu dodać po prostu żeby sprawdzić czy działą czy zrobić że jak się kliknie
        if(slot == 4){                          //  w tym wypadku slot 4 i jeśli gracz go kilknie to dostaje komunikat "dziala
            player.sendMessage("Dziala");    // jest to po e.setCancelled(true) bo to już jest tylko liczenie (int slots = e.getSlot(); musi być przed e.setCancelled bo inaczej by nie było rejestorwane
        }
    }

     @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) { // //bierzemy InventoryClose w argument (przypisujemy go jako e)
        if (!(e.getViewers() instanceof Player player)) return; // również sprawdzamy czy byt który wykonał event jest graczem ( i przy okazji przypisujemy gracza)
        Inventory inv = e.getInventory(); // przypisujemy jakie inventory jest otwarte

        if (guiInventories.containsKey(player.getUniqueId()) && guiInventories.get(player.getUniqueId()).equals(inv)) { // tak jak wcześniej sprawdzamy czy inventory które gracz otwarł jest gracza i czy jest to poprawne inventory (żeby nie blokowac innych inventory)
            player.sendMessage(ChatColor.RED + "Zamknąłeś GUI!");  // test do sprawdzenia czy kod działa
            guiInventories.remove(player.getUniqueId());  // najważniejsza część tego kodu czyli usunięcie gracza z listy graczy którzy otwarli gui żeby potemy się nic nie glichowało
        }
    }

}
