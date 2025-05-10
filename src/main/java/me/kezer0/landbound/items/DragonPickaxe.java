package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class DragonPickaxe {

    public static ItemStack createDragonPickaxe() {

        ItemStack dragonPickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = dragonPickaxe.getItemMeta();


        meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SMOCZY KILOF");

        // Add lore
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "MYTHIC");
        lore.add(ChatColor.GRAY + "Starożytna moc smoków");
        lore.add(ChatColor.GREEN + "Funkcje Specjalne:");
         lore.add(ChatColor.DARK_PURPLE + "Niezniszczalny");
        lore.add(ChatColor.DARK_PURPLE + "Auto-Naprawa");
        lore.add(ChatColor.DARK_PURPLE + "Szybkie kopanie");
        meta.setLore(lore);


        meta.setUnbreakable(true);


        meta.addEnchant(Enchantment.EFFICIENCY, 5, true); 
        meta.addEnchant(Enchantment.UNBREAKING, 3, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);


        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);


        dragonPickaxe.setItemMeta(meta);

        return dragonPickaxe;
    }
}

