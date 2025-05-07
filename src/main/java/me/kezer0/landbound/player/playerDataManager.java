package me.kezer0.landbound.player;

import me.kezer0.landbound.database.databaseHelper;
import me.kezer0.landbound.land.generation.worldCreator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class playerDataManager {
//W PÓŹNIEJSZYM UŻYDKU
    public static void handlePlayerJoin(Player player) {

        UUID uuid = player.getUniqueId();
        File playerFolder = new File("plugins/LandBound/players/" + uuid);
        File islandFile = new File(playerFolder, "island.yml");

        if (!playerFolder.exists()) {
            playerFolder.mkdirs();
        }


        if (!islandFile.exists()) {
            Bukkit.getLogger().info("[LandBound] Tworzenie świata wyspy dla gracza " + player.getName());
            worldCreator.createIslandWorld(player);
        } else {

            if (Bukkit.getWorld(uuid.toString()) == null) {

                worldCreator.createIslandWorld(player);
            }
        }
    }

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS players (
                uuid TEXT PRIMARY KEY,
                balance DOUBLE DEFAULT 0,
                level INTEGER DEFAULT 0,
                experience DOUBLE DEFAULT 0
            );
        """;

        try (Connection conn = databaseHelper.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createPlayerIfNotExists(UUID uuid) {

        String checkSql = "SELECT uuid FROM players WHERE uuid = ?";
        String insertSql = "INSERT INTO players (uuid) VALUES (?)";

        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, uuid.toString());
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {

                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                    insertStmt.setString(1, uuid.toString());
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void savePlayerData(UUID uuid, double balance, int level, double experience) {

        String sql = "UPDATE players SET balance = ?, level = ?, experience = ? WHERE uuid = ?";

        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, balance);
            stmt.setInt(2, level);
            stmt.setDouble(3, experience);
            stmt.setString(4, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double getBalance(UUID uuid) {
        return getDouble(uuid, "balance");
    }

    public static int getLevel(UUID uuid) {
        return getInt(uuid, "level");
    }

    public static double getExperience(UUID uuid) {
        return getDouble(uuid, "experience");
    }

    private static double getDouble(UUID uuid, String column) {

        String sql = "SELECT " + column + " FROM players WHERE uuid = ?";

        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                return rs.getDouble(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int getInt(UUID uuid, String column) {

        String sql = "SELECT " + column + " FROM players WHERE uuid = ?";

        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                return rs.getInt(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}