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

public class MythicSword implements Listener {


    public static ItemStack createMythicSword() {
        ItemStack mythicSword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = mythicSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "MITYCZNY MIECZ");
            meta.setLore(List.of(
                ChatColor.GRAY + "Prawdziwa moc nie leży w tym, co widać, ale w tym,",
                ChatColor.GRAY + "co potrafisz schować.",
                "",
                ChatColor.YELLOW + "Tryby:",
                ChatColor.AQUA + "- Strateg: Zwiększa obrażenia o 20% przy zmniejszonym cooldownie na ataki.",
                ChatColor.AQUA + "- Mistrz Kontroli: Daje odporność na efekty statusu przez 10 sekund (cooldown 30 sekund).",
                ChatColor.AQUA + "- Tryb Regeneracji: Regeneracja 1 przez 5 sekund.",
                ChatColor.AQUA + "- Moc Cienia: Zwiększa szansę na krytyczny cios o 15% na 10 sekund (cooldown 1 minuta)."
            ));

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            mythicSword.setItemMeta(meta);
        }
        return mythicSword;
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player player && damaged instanceof LivingEntity) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() &&
                    itemInHand.getItemMeta().getDisplayName().equals(ChatColor.RED + "Mityczny Miecz")) {

                
                int mode = 0; 

                switch (mode) {
                    case 0: // Strateg
                        double origDamage = event.getDamage();
                        event.setDamage(origDamage * 1.2); 
                        player.sendMessage(ChatColor.GREEN + "Tryb Strateg aktywowany! +20% obrażeń.");
                        break;
                    case 1: // Mistrz Kontroli
                        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 0));
                        player.sendMessage(ChatColor.GREEN + "Tryb Mistrz Kontroli aktywowany! Odporność na efekty statusu.");
                        break;
                    case 2: // Tryb Regeneracji
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
                        player.sendMessage(ChatColor.GREEN + "Tryb Regeneracji aktywowany! Regeneracja na 5 sekund.");
                        break;
                    case 3: // Moc Cienia
                        if (Math.random() < 0.15) {
                            double critBonus = event.getDamage() * 0.5; 
                            event.setDamage(event.getDamage() + critBonus);
                            player.sendMessage(ChatColor.GREEN + "Moc Cienia: Krytyczny cios!");
                        } else {
                            player.sendMessage(ChatColor.GREEN + "Moc Cienia nie zadziałała tym razem.");
                        }
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Wybrano niepoprawny tryb.");
                        break;
                }
            }
        }
    }
}
