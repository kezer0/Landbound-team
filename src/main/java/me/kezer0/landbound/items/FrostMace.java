
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
import java.util.Random;

public class FrostMace implements Listener {

    public static ItemStack createFrostMace() {
        ItemStack frostmace = new ItemStack(Material.MACE);
        ItemMeta meta = frostmace.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "LODOWY TOPÓR");
            meta.addEnchant(Enchantment.SHARPNESS, 3, true); 
            meta.setLore(List.of(
                ChatColor.AQUA + "RARE",
                ChatColor.GRAY + "Topór wykuty z lodu",
                ChatColor.GRAY + "Cios nim może przynieść przewagę w walce",
                "",
                ChatColor.GREEN + "Funkcje Specjalne:",
                ChatColor.AQUA + "- 15% szansy na nałożenie " + ChatColor.DARK_PURPLE + "Oslabienia I" + ChatColor.AQUA + " na 5 sekund",
                ChatColor.AQUA + "- Szybkość I przez 3 sekundy po trafieniu"
            ));

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            frostmace.setItemMeta(meta);
        }
        return frostmace;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player player && damaged instanceof LivingEntity target) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.hasItemMeta()
                    && item.getItemMeta().hasDisplayName()
                    && item.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "" + ChatColor.BOLD + "Lodowy Topór")) {


                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0)); // 3 sekundy

                if (new Random().nextDouble() <= 0.15) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0)); // 5 sekund Weakness I
                }
            }
        }
    }
}



