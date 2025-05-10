package me.kezer0.landbound.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RavenDaggerListener implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey daggerKey;

    public RavenDaggerListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.daggerKey = new NamespacedKey(plugin, "raven_dagger");
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        // Sprawdzamy czy cel jest żywą istotą
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return;

        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!data.has(daggerKey, PersistentDataType.BYTE)) return;

        // Skoro przeszedł wszystkie warunki, nakładamy efekty
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1)); // 5 sekund Withera II
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 0)); // 5 sekund Darkness I
    }
}