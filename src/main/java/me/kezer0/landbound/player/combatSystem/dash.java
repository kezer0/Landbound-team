package me.kezer0.landbound.player.combatSystem;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class dash implements Listener {
    private final Map<UUID, Long> dashCooldown = new HashMap<>();
    private final Map<UUID, Long> lastTapTime = new HashMap<>();
    private final Set<UUID> waitingForKeyRelease = new HashSet<>();
    private final long DOUBLE_TAP_TIME = 250;
    private final long COOLDOWN = 3000;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || from == null) return;
        if (!player.isOnGround()) return;


        if (from.getX() == to.getX() && from.getZ() == to.getZ()) {
            if (waitingForKeyRelease.contains(uuid)) {
                player.sendMessage("§7[DEBUG] Puściłeś W – możesz tapnąć ponownie");
                waitingForKeyRelease.remove(uuid);
            }
            return;
        }

        // Sprawdź kierunek
        Vector movement = to.toVector().subtract(from.toVector()).normalize();
        Vector facing = player.getLocation().getDirection().setY(0).normalize();
        double dot = movement.dot(facing);

        if (dot < 0.90) {
            // Nie idzie prosto
            waitingForKeyRelease.remove(uuid);
            return;
        }

        if (waitingForKeyRelease.contains(uuid)) {
            // Nadal trzyma W
            return;
        }

        // Teraz to pierwszy tap
        waitingForKeyRelease.add(uuid);
        long now = System.currentTimeMillis();

        if (dashCooldown.containsKey(uuid) && now - dashCooldown.get(uuid) < COOLDOWN) {
            player.sendMessage("§c[DEBUG] Dash na cooldownie");
            return;
        }

        if (lastTapTime.containsKey(uuid) && now - lastTapTime.get(uuid) <= DOUBLE_TAP_TIME) {
            // Double-tap wykryty
            Vector dashVector = facing.multiply(2);
            dashVector.setY(0.2);
            player.setVelocity(dashVector);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.2f);
            dashCooldown.put(uuid, now);
            lastTapTime.remove(uuid);
            player.sendMessage("§a[DEBUG] DASH!");
        } else {
            // Pierwszy tap
            lastTapTime.put(uuid, now);
            player.sendMessage("§e[DEBUG] 1szy TAP zapisany");
        }
    }
}

