package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemFlag;
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

public class UraniumSword implements Listener {

    public static ItemStack createUraniumSword() {
        ItemStack uraniumsword = new ItemStack(Material.NETHERITE_SWORD); 
        ItemMeta meta = uraniumsword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "URANOWY MIECZ");
            meta.addEnchant(Enchantment.SHARPNESS, 5, true); 
            meta.setLore(List.of(
                ChatColor.DARK_PURPLE + "EPIC",
                ChatColor.DARK_PURPLE + "Miecz wykuty z czystego uranu.",
                ChatColor.GREEN + "Funkcje Specjalne:",
                ChatColor.DARK_PURPLE + "- Zatruwa przeciwników po uderzeniu!",
                ChatColor.DARK_PURPLE + "- Posiada Sharpness V"
            ));

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            uraniumsword.setItemMeta(meta);
        }
        return uraniumsword;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player player && damaged instanceof LivingEntity livingEntity) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand != null && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()) {
                if (itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "URANOWY MIECZ")) {
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1)); // 5 sekund zatrucia
                }
            }
        }
    }
}
