Okej, ziomek – poniżej znajdziesz zaktualizowany kod MythicSword.java, w którym zamiast losowego wyboru trybu mamy statyczną mapę przechowującą wybrany przez gracza tryb. Komenda /zmientryb oraz GUI (MysticSModesGUI.java) powinny gdzieś wywoływać metodę ustawiającą tryb dla danego gracza, np. przez wywołanie metody setPlayerMode(Player, int).

package me.kezer0.landbound;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MythicSword implements Listener {

    // Mapa przechowująca wybrany tryb dla każdego gracza (0: Strateg, 1: Mistrz Kontroli, 2: Tryb Regeneracji, 3: Moc Cienia)
    private static final Map<UUID, Integer> playerModes = new HashMap<>();

    
    public static void setPlayerMode(Player player, int mode) {
        playerModes.put(player.getUniqueId(), mode);
        player.sendMessage(ChatColor.GREEN + "Wybrano tryb: " + getModeName(mode));
    }

   
    public static int getPlayerMode(Player player) {
        return playerModes.getOrDefault(player.getUniqueId(), 0);
    }

   
    private static String getModeName(int mode) {
        return switch (mode) {
            case 0 -> "Strateg";
            case 1 -> "Mistrz Kontroli";
            case 2 -> "Tryb Regeneracji";
            case 3 -> "Moc Cienia";
            default -> "Nieznany";
        };
    }

    
    public static ItemStack createMythicSword() {
        ItemStack mythicSword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = mythicSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Mityczny Miecz");
            meta.addEnchant(Enchantment.SHARPNESS, 5, true); // Efekt: Sharpness 5

            meta.setLore(List.of(
                ChatColor.GRAY + "Prawdziwa moc nie leży w tym, co widać, ale w tym,",
                ChatColor.GRAY + "co potrafisz schować.",
                "",
                ChatColor.YELLOW + "Tryby:",
                ChatColor.AQUA + "Strateg: Zwiększa obrażenia o 20% przy zmniejszonym cooldownie na ataki.",
                ChatColor.AQUA + "Mistrz Kontroli: Daje odporność na efekty statusu przez 10 sekund (cooldown 30 sekund).",
                ChatColor.AQUA + "Tryb Regeneracji: Regeneracja 1 przez 5 sekund.",
                ChatColor.AQUA + "Moc Cienia: Zwiększa szansę na krytyczny cios o 15% na 10 sekund (cooldown 1 minuta)."
            ));
            mythicSword.setItemMeta(meta);
        }
        return mythicSword;
    }

    /**
     * Obsługuje event uderzenia przeciwnika przez gracza.
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player player && damaged instanceof LivingEntity livingEntity) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() &&
                itemInHand.getItemMeta().getDisplayName().equals(ChatColor.RED + "Mityczny Miecz")) {

                // Pobieramy wybrany tryb dla gracza (domyślnie 0: Strateg)
                int mode = getPlayerMode(player);

                switch (mode) {
                    case 0: // Strateg
                        double origDamage = event.getDamage();
                        event.setDamage(origDamage * 1.2); // Zwiększenie obrażeń o 20%
                        player.sendMessage(ChatColor.GREEN + "Tryb Strateg aktywowany! +20% obrażeń.");
                        break;
                    case 1: // Mistrz Kontroli
                        // Odporność na efekty statusu przez 10 sekund (200 ticków)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
                        player.sendMessage(ChatColor.GREEN + "Tryb Mistrz Kontroli aktywowany! Odporność na efekty statusu.");
                        break;
                    case 2: // Tryb Regeneracji
                        // Regeneracja 1 przez 5 sekund (100 ticków)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
                        player.sendMessage(ChatColor.GREEN + "Tryb Regeneracji aktywowany! Regeneracja na 5 sekund.");
                        break;
                    case 3: // Moc Cienia
                        // Szansa na krytyczny cios: 15%
                        if (Math.random() < 0.15) {
                            double critBonus = event.getDamage() * 0.5; // dodatkowe 50% obrażeń
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



