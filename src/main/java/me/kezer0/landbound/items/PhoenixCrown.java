// legendary item
// Mythic Item
package me.kezer0.landbound;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PhoenixCrown implements Listener {

    /**
     * Tworzy niestandardowy miecz obsydianowy.
     * @return ItemStack reprezentujący miecz obsydianowy
     */
    public static ItemStack PhoenixCrown() {
        ItemStack phoenixcrown = new ItemStack(Material.NETHERITE_HELMET); 
        ItemMeta meta = phoenixcrown.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.ORANGE + "Korona Feniksa");
            meta.addEnchant(Enchantment.SHARPNESS, 5, true); 
            meta.setLore(List.of(
                ChatColor.GRAY + "Korona mająca wiele funckji .",
                ChatColor.GREEN + "Funkcje Specjalne : 
                tryby: Strateg, Kontrola, Regeneracja"
                
            ));
            phoenixcrown.setItemMeta(meta);
        }
        return phoenixcrown;
    }

    /**
     * Obsługuje event uderzenia przeciwnika przez gracza.
     * Jeśli gracz używa itemyb, nakłada efekt zatrucia na przeciwnika.
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player player && damaged instanceof LivingEntity livingEntity) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() &&
                itemInHand.getItemMeta().getDisplayName().equals(ChatColor.ORANGE + "Korona Feniksa")) {

                // Nakładamy efekt zatrucia na przeciwnika
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1)); // 5 sekund zatrucia
            }
        }
    }
}