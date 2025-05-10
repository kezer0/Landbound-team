package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemFlag;
import me.kezer0.landbound.utils.CColor;
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
import java.util.UUID;

public class MysteriousHelmet {

    public static ItemStack createMysteriousHelmet() {


        ItemStack mysterioushelmet = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = mysterioushelmet.getItemMeta();


        meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "TAJEMNICZY HEŁM");

        List<String> lore = new ArrayList<>();
lore.add(ChatColor.DARK_RED + "MYTHIC");
lore.add(ChatColor.GRAY + "Nie wiadomo, kto był jego właścicielem, ale jego magia wciąż tętni życiem.");
lore.add(ChatColor.GREEN + "- Funkcje Specjalne:");
lore.add(ChatColor.GRAY + "- Niezniszczalny");
lore.add(ChatColor.GRAY + "- Auto-Naprawa");
lore.add(ChatColor.GRAY + "- Ochrona VI");
lore.add(ChatColor.RED + "- Regeneracja zdrowia co 10 sekund.");
lore.add(ChatColor.BLUE + "- Niewidzialność na 5 sekund po otrzymaniu obrażeń.");
lore.add(ChatColor.DARK_RED + "- Odporność na ogień i lawę."); 
lore.add(ChatColor.RED + "- Zwiększa maksymalne zdrowie o 4 serca.");
lore.add(ChatColor.DARK_RED + "- Odbija przeciwników po ataku.");

meta.setLore(lore);

        // Atrybuty itemu 
        meta.setUnbreakable(true); 

        meta.addEnchant(Enchantment.MENDING, 1, true); 
        meta.addEnchant(Enchantment.PROTECTION, 6, true); 

        // Atrybut zdrowia: +4 serca
        AttributeModifier healthBoost = new AttributeModifier(
            UUID.randomUUID(), 
            "generic.maxHealth", 
            8.0, // 8.0 = 4 serca 
            AttributeModifier.Operation.ADD_NUMBER
        );
        meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthBoost);


        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);


        mysterioushelmet.setItemMeta(meta);

        return mysterioushelmet;
    }
}