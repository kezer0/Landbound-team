package me.kezer0.landbound.listeners;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.kezer0.landbound.items.FrostMace;

import java.util.Random;

import me.kezer0.landbound.Landbound;

public class FrostMaceListener implements Listener {

    private final NamespacedKey frostMaceKey = new NamespacedKey(Landbound.getInstance(), "frost_mace");

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (!(damager instanceof Player player) || !(damaged instanceof LivingEntity target)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (!container.has(frostMaceKey, PersistentDataType.BYTE)) return;

        // Speed I for 3 sec
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0));

        // 15% chance for Weakness I for 5 sec
        if (new Random().nextDouble() <= 0.15) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
        }
    }
}