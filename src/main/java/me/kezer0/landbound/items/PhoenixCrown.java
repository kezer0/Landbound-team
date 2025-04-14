package me.kezer0.landbound;

import org.bukkit.entity.Entity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.attribute.Attribute;

import java.util.List;

public class PhoenixCrown implements Listener {
    // Cooldowny
    private static final long REBIRTH_COOLDOWN = 60000; // 60 sekund na odrodzenie
    private static final long REGENERATION_COOLDOWN = 30000; // 30 sekund na regenerację
    private static final long DAMAGE_BOOST_COOLDOWN = 60000; // 1 minuta na zwiększenie obrażeń
    private static final long CLEANSE_COOLDOWN = 60000; // 1 minuta na usunięcie negatywnych efektów

    /**
     * @return ItemStack reprezentujący Koronę Feniksa
     */
    public static ItemStack createPhoenixCrown() {
        ItemStack phoenixCrown = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = phoenixCrown.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Korona Feniksa"); 

            meta.addEnchant(Enchantment.PROTECTION_FIRE, 4, true); 
            meta.setLore(List.of(
                ChatColor.YELLOW + "LEGENDARY",
                ChatColor.GRAY + "Korona Feniksa posiada niezwykłe moce.",
                "", 
                ChatColor.GREEN + "Funkcje Specjalne:",
                ChatColor.ORANGE + "Odrodzenie",
                ChatColor.ORANGE + "Regeneracja",
                ChatColor.ORANGE + "Zwiększenie Obrażeń",
                ChatColor.ORANGE + "Usuwanie Negatywnych Efektów"
            ));
            phoenixCrown.setItemMeta(meta);
        }
        return phoenixCrown;
    }

    // Funkcja obsługująca obrażenia zadawane przez gracza
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player player && damaged instanceof LivingEntity livingEntity) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            // Sprawdź, czy gracz używa Korony Feniksa
            if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() &&
                itemInHand.getItemMeta().getDisplayName().equals(ChatColor.ORANGE + "Korona Feniksa")) {

                // Zwiększenie regeneracji
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 2)); // Regeneracja 2 przez 6 sekund
                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("YourPluginName"), REGENERATION_COOLDOWN);

                // Wzmocnienie obrażeń - bonus do obrażeń poniżej 50% zdrowia
                if (player.getHealth() < player.getMaxHealth() / 2) {
                    double bonusDamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() * 0.1;
                    player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() + bonusDamage);
                }
            }
        }
    }

    // Funkcja odrodzenia gracza po śmierci
    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        Player player = (Player) event.getEntity();

        ItemStack itemInHead = player.getInventory().getHelmet();

        if (itemInHead != null && itemInHead.hasItemMeta() && 
            itemInHead.getItemMeta().getDisplayName().equals(ChatColor.ORANGE + "Korona Feniksa")) {

            // Funkcja odrodzenia
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getHealth() <= 0) {
                        player.setHealth(player.getMaxHealth());
                        player.teleport(player.getLocation()); // Odrodzenie w miejscu śmierci
                    }
                }
            }.runTaskLater(Bukkit.getPluginManager().getPlugin("LandBound"), 100L); // Odrodzenie po 5 sekundach

            // Anulowanie upuszczenia przedmiotów
            event.getDrops().clear();
        }
    }

    // Funkcja usuwania negatywnych efektów co minutę
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHead = player.getInventory().getHelmet();

        if (itemInHead != null && itemInHead.hasItemMeta() && 
            itemInHead.getItemMeta().getDisplayName().equals(ChatColor.ORANGE + "Korona Feniksa")) {

            // Usuwanie mrocznych efektów co minutę
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        if (effect.getType().isBad()) { // Sprawdź, czy efekt jest negatywny
                            player.removePotionEffect(effect.getType()); // Usuń negatywne efekty
                        }
                    }
                }
            }.runTaskLater(Bukkit.getPluginManager().getPlugin("LandBound"), CLEANSE_COOLDOWN);
        }
    }
}

