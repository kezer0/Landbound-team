package me.kezer0.landbound.player;

import java.util.UUID;

public class playerStatistics {
    private final UUID uuid;
    private double health;
    private int damage;
    private int level;

    public playerStatistics(UUID uuid, double health, int damage, int level) {
        this.uuid = uuid;
        this.health = health;
        this.damage = damage;
        this.level = level;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
