package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import me.kezer0.landbound.utils.CColor;

import java.util.List;
import java.util.Random;

public class ThunderAxe implements Listener {

    private static final Random random = new Random();


    public static ItemStack createZeusAxe() {
        ItemStack thunderaxe = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = thunderaxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.GOLD + "SIEKIERKA ZEUSA");
            meta.setLore(List.of(
                ChatColor.YELLOW + "LEGENDARY",
                ChatColor.GOLD + "Wykuwana z dusz burzy i gniewu Zeusa.",
                ChatColor.GOLD + "Tylko nieliczni potrafią znieść jej moc.",
                ChatColor.GREEN + "Efekty specjalne:",
                ChatColor.GOLD + " - Szansa na uderzenie pioruna.",
                ChatColor.GOLD + " - Odpycha wrogów przy trafieniu.",
                ChatColor.DARK_RED + " Nosi piętno Boga Piorunów."
            ));

            thunderaxe.setItemMeta(meta);


        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        return thunderaxe;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        Player player = (Player) event.getDamager();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.NETHERITE_AXE || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) return;

        if (!ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Siekiera Zeusa")) return;

        // 30% szansy na uderzenie pioruna
        if (random.nextDouble() <= 0.3) {
            event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
        }

        // Odepchnięcie przeciwnika
        Entity target = event.getEntity();
        Vector knockback = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.5);
        knockback.setY(0.5); // Dodajemy efekt odrzutu w górę
        target.setVelocity(knockback);

        // Efekt dźwiękowy
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
    }
}
