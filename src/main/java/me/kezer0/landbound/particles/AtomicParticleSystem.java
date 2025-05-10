package me.kezer0.landbound.particles;

import org.bukkit.util.Vector;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AtomicParticleSystem {

    private final JavaPlugin plugin;

    public AtomicParticleSystem(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawnAtomicParticles(Player player) {
        new BukkitRunnable() {
            double angle = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                Location loc = player.getLocation().add(0, 1, 0); 
                World world = loc.getWorld();

                double radius = 0.5;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;

                loc.add(x, 0, z);
                world.spawnParticle(Particle.END_ROD, loc, 0, 0, 0, 0);
                loc.subtract(x, 0, z);

                angle += Math.PI / 16;
            }
        }.runTaskTimer(plugin, 0L, 2L); 
    }
}
