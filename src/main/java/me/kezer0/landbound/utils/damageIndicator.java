package me.kezer0.landbound.utils;

import me.kezer0.landbound.Landbound;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class damageIndicator {

    public static final JavaPlugin plugin = Landbound.getPlugin(Landbound.class);

    public static void createDamageIndicator(Location loc, double damage, boolean crit){
        String text;

        if (crit) {
            text = "§c§l" + (int) damage + "§f§l✧";
        } else {
            text = "§7" + (int) damage;
        }

        Location tempLocation = loc.clone().subtract(0, 1000, 0);
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(tempLocation, EntityType.ARMOR_STAND);

        as.setCustomName(text);
        as.setCustomNameVisible(true);
        as.setVisible(false);
        as.setSmall(true);
        as.setGravity(false);
        as.setMarker(true);
        as.setInvulnerable(true);
        as.setSilent(true);
        as.setBasePlate(false);

        as.teleport(loc);

        final int duration = 20;
        final double moveUpPerTick = 0.03;

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration || as.isDead()) {
                    as.remove();
                    cancel();
                    return;
                }

                Location current = as.getLocation();
                current.add(0, moveUpPerTick, 0);
                as.teleport(current);

                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
