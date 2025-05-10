package me.kezer0.landbound.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.entity.Player;

import me.kezer0.landbound.Landbound; 

public class DragonArmorListener implements Listener {

    private final Landbound plugin;
    private final NamespacedKey dragonArmorKey;

    public DragonArmorListener(Landbound plugin) {
        this.plugin = plugin;
        this.dragonArmorKey = new NamespacedKey(plugin, "dragon_armor");
    }

    public ItemStack createDragonArmor() {
        ItemStack armor = new ItemStack(Material.NETHERITE_CHESTPLATE); 
        ItemMeta meta = armor.getItemMeta();

        if (meta != null) {
            meta.setUnbreakable(true);

            meta.addEnchant(Enchantment.UNBREAKING, 3, true);
            meta.addEnchant(Enchantment.MENDING, 1, true);
            meta.addEnchant(Enchantment.PROTECTION, 6, true);

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

         
            meta.getPersistentDataContainer().set(dragonArmorKey, PersistentDataType.BYTE, (byte) 1);

            armor.setItemMeta(meta);
        }

        return armor;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || !chestplate.hasItemMeta()) return;

        ItemMeta meta = chestplate.getItemMeta();
        if (meta.getPersistentDataContainer().has(dragonArmorKey, PersistentDataType.BYTE)) {
           
            double damage = event.getDamage();
            event.setDamage(damage * 0.8);
        }
    }
}