package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
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

import java.util.List;

public class PhoenixCrown implements Listener {
    // Cooldowny
    private static final long REBIRTH_COOLDOWN = 60000; // 60 sekund na odrodzenie
    private static final long REGENERATION_COOLDOWN = 30000; // 30 sekund na regenerację
    private static final long DAMAGE_BOOST_COOLDOWN = 60000; // 1 minuta na zwiększenie obrażeń
    private static final long CLEANSE_COOLDOWN = 60000; // 1 minuta na usunięcie negatywnych efektów


    public static ItemStack createPhoenixCrown() {
        ItemStack phoenixCrown = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = phoenixCrown.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "KORONA FENIKSA");

            meta.addEnchant(Enchantment.FIRE_PROTECTION, 4, true);
            meta.setLore(List.of(
                    ChatColor.YELLOW + "LEGENDARY",
                    ChatColor.GRAY + "Korona Feniksa posiada niezwykłe moce.",
                    "",
                    ChatColor.GOLD + "- Funkcje Specjalne:",
                    ChatColor.GOLD + "- Odrodzenie",
                    ChatColor.GOLD + "- Regeneracja",
                    ChatColor.GOLD + "- Zwiększenie Obrażeń",
                    ChatColor.GOLD + "- Usuwanie Negatywnych Efektów"
            ));

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            phoenixCrown.setItemMeta(meta);
        }
        return phoenixCrown;
    }

    // Funkcja obsługująca obrażenia zadawane przez gracza
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player && damaged instanceof LivingEntity) {
            Player player = (Player) damager;
            LivingEntity livingEntity = (LivingEntity) damaged;

            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() &&
                    itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "" + ChatColor.BOLD + "Korona Feniksa")) {

                // Zwiększenie regeneracji
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 2));
                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("LandBound"), REGENERATION_COOLDOWN / 50); // /50 bo czas w tickach

                // Wzmocnienie obrażeń - bonus do obrażeń poniżej 50% zdrowia
                if (player.getHealth() < player.getMaxHealth() / 2) {
                    double currentDamage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
                    double bonusDamage = currentDamage * 0.1;
                    player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(currentDamage + bonusDamage);
                }
            }
        }
    }

    // Funkcja odrodzenia gracza po śmierci
    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        ItemStack itemInHead = player.getInventory().getHelmet();

        if (itemInHead != null && itemInHead.hasItemMeta() &&
                itemInHead.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "" + ChatColor.BOLD + "Korona Feniksa")) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getHealth() <= 0) {
                        player.setHealth(player.getMaxHealth());
                        player.teleport(player.getLocation());
                    }
                }
            }.runTaskLater(Bukkit.getPluginManager().getPlugin("LandBound"), 100L);

            event.getDrops().clear();
        }
    }

   
@EventHandler
public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    ItemStack itemInHead = player.getInventory().getHelmet();  

    // Sprawdzamy, czy gracz ma hełm i czy ma odpowiednią nazwę
    if (itemInHead != null && itemInHead.hasItemMeta() &&
            itemInHead.getItemMeta().getDisplayName().equals("Korona Feniksa")) {  
        new BukkitRunnable() {
            @Override
            public void run() {
                // Przechodzimy przez aktywne efekty gracza
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    // Sprawdzamy, czy efekt jest negatywny (np. zatrucie, osłabienie)
                    if (isNegativeEffect(effect.getType())) {
                        player.removePotionEffect(effect.getType());  
                    }
                }
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("LandBound"), 20L);  
}

// Metoda pomocnicza do sprawdzania, czy efekt jest negatywny
private boolean isNegativeEffect(PotionEffectType effectType) {
    // Lista negatywnych efektów
    return effectType == PotionEffectType.POISON ||
           effectType == PotionEffectType.WITHER ||
           effectType == PotionEffectType.SLOWNESS ||
           effectType == PotionEffectType.MINING_FATIGUE ||
           effectType == PotionEffectType.BLINDNESS ||
           effectType == PotionEffectType.NAUSEA ||
           effectType == PotionEffectType.INSTANT_DAMAGE ||
           effectType == PotionEffectType.WEAKNESS ||
           effectType == PotionEffectType.BAD_OMEN;
           }
   }
