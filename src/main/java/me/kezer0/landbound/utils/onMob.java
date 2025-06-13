package me.kezer0.landbound.utils;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

import static me.kezer0.landbound.player.playerListener.entityHealth;

public class onMob {
    private static final Map<Attribute,Number> attributes = new HashMap<>();

    public static void summonedMob(LivingEntity entity, double hp, int dmg){
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
        entity.setHealth(hp);
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(dmg);
        entity.getAttribute(Attribute.GENERIC_BURNING_TIME).setBaseValue(0);
        entity.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER).setBaseValue(0);
    }

    public static void updateCustomName(LivingEntity entity){
        String name = entity.getType().name().substring(0, 1) + entity.getType().name().substring(1).toLowerCase();

        double hp = entityHealth.getOrDefault(entity.getUniqueId(), entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        int maxHp = (int) entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        int currentHp = (int) Math.max(0, Math.min(hp, maxHp));

        StringBuilder customName = new StringBuilder();
        customName.append(ChatColor.GREEN).append(name)
                  .append(" ")
                  .append(ChatColor.GRAY).append("[")
                  .append(ChatColor.RED).append(currentHp)
                  .append("/")
                  .append(maxHp)
                  .append(ChatColor.GRAY).append("]");

        entity.setCustomName(customName.toString());
        entity.setCustomNameVisible(true);
    }
}

