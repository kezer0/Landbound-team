package me.kezer0.landbound.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class DragonPickaxeListener implements Listener {

    private final JavaPlugin plugin;

    public DragonPickaxeListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "dragon_pickaxe");

        if (container.has(key, PersistentDataType.STRING) && container.get(key, PersistentDataType.STRING).equals("true")) {
            player.getWorld().spawnParticle(Particle.FLAME, event.getBlock().getLocation().add(0.5, 1, 0.5), 10, 0.2, 0.5, 0.2, 0.01);
            player.getWorld().playSound(event.getBlock().getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5f, 2.0f);
        }
    }
}