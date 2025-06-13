package me.kezer0.landbound.player;

import me.kezer0.landbound.Landbound;
import me.kezer0.landbound.land.blocks.blockSaver;
import me.kezer0.landbound.land.database.databaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.kezer0.landbound.land.generation.worldCreator.unloadAndDeleteWorld;
import static me.kezer0.landbound.utils.damageIndicator.createDamageIndicator;
import static me.kezer0.landbound.utils.onMob.updateCustomName;

public class playerListener implements Listener {
    public static Map<UUID, Double> entityHealth = new HashMap<>();
    private final Map<UUID, Long> lastHitTick = new HashMap<>();

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        playerDataManager.initPlayer(e.getPlayer());
        World world = Bukkit.getWorld("world");
        Location location = new Location(world, 1, 100, 1);
        e.getPlayer().teleportAsync(location);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        blockSaver.flushBufferToDisk(uuid);
        playerDataManager.savePlayer(player);
        playerDataManager.removePlayer(player);
        unloadAndDeleteWorld(player);
        databaseManager.close();
    }

    @EventHandler
    private void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        if (e.getEntity() instanceof Player) return;
        if (!(e.getEntity() instanceof LivingEntity livingEntity)) return;

        playerStatistics stats = playerDataManager.getStats(player);
        double damage = stats != null ? stats.getDamage() : 1;

        UUID uuid = livingEntity.getUniqueId();
        long currentTick = Bukkit.getCurrentTick();

        if (lastHitTick.containsKey(uuid) && currentTick - lastHitTick.get(uuid) < 10) {
            e.setCancelled(true);
            return;
        }

        lastHitTick.put(uuid, currentTick);

        double currentHealth = entityHealth.getOrDefault(
                uuid,
                livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()
        );

        double entityMaxHealth = livingEntity.getMaxHealth();
        double finalDmg = damage;
        boolean crit = e.isCritical();

        currentHealth -= finalDmg;
        createDamageIndicator(livingEntity.getLocation().add(0, 1.5, 0), damage, crit);

        if (currentHealth <= 0) {
            entityHealth.put(uuid, 0.0);
            updateCustomName(livingEntity);
            Bukkit.getScheduler().runTaskLater(Landbound.getInstance(), () -> {
                livingEntity.setHealth(0);
            }, 1L);
            e.setCancelled(true);
        } else {
            e.setDamage(0);
            entityHealth.put(uuid, currentHealth);
            livingEntity.setHealth(entityMaxHealth);
            updateCustomName(livingEntity);
        }
    }

    @EventHandler
    private void food(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player player) {
            e.setCancelled(true);
            player.setFoodLevel(20);
            player.setExhaustion(0);
        }
    }

    @EventHandler
    private void onDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player player) {
            String name = player.getName();
            Bukkit.broadcastMessage(ChatColor.RED + name + " zginął");
        }
    }
}
