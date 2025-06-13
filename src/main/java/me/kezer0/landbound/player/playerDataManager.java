package me.kezer0.landbound.player;

import me.kezer0.landbound.Landbound;
import me.kezer0.landbound.land.database.databaseManager;
import me.kezer0.landbound.player.combatSystem.dash;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.kezer0.landbound.player.playerNameTag.setPlayerTag;

public class playerDataManager {
    private static final Map<UUID, playerStatistics> playerStatsMap = new HashMap<>();

    public static void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        playerStatistics stats = loadStatsFromDatabase(uuid);
        playerStatsMap.put(uuid, stats);

        player.setHealth(stats.getHealth());
        player.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER).setBaseValue(0);
        player.setFoodLevel(20);
        player.setExhaustion(0);
        setPlayerTag(player);
        Bukkit.getPluginManager().registerEvents(new dash(), Landbound.getInstance());
    }

    public static void savePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        playerStatistics stats = playerStatsMap.get(uuid);
        if (stats == null) return;

        try (Connection conn = databaseManager.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT OR REPLACE INTO players (uuid, level, health, damage) VALUES (?, ?, ?, ?)"
            );

            stmt.setString(1, uuid.toString());
            stmt.setInt(2, stats.getLevel());
            stmt.setDouble(3, stats.getHealth());
            stmt.setInt(4, stats.getDamage());
            stmt.addBatch();

            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static playerStatistics loadStatsFromDatabase(UUID uuid) {
        String sql = "SELECT * FROM players WHERE uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int level = rs.getInt("level");
                    double health = rs.getDouble("health");
                    int damage = rs.getInt("damage");

                    return new playerStatistics(uuid, health, damage, level);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new playerStatistics(uuid, 20, 1, 1); // domyślne statystyki
    }

    public static playerStatistics getStats(Player player) {
        return playerStatsMap.get(player.getUniqueId());
    }

    public static void removePlayer(Player player) {
        playerStatsMap.remove(player.getUniqueId());
    }

}
