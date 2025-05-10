package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class LifePotion {

    public static ItemStack createLifePotion() {

        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "MIKSTURA ŻYCIA");


            meta.setLore(List.of(
                ChatColor.DARK_PURPLE + "EPIC",
                ChatColor.RED + "Mikstura dająca wiele zdrowia",
                ChatColor.GREEN + "Funkcja specjalna: ",
                ChatColor.RED + "- Daje +4 serca na 90s"
            ));

            // Ukrycie atrybutów
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            // Dodanie efektu HEALTH_BOOST
            meta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1800, 1), true); 
            potion.setItemMeta(meta);
        }

        return potion;
    }
}