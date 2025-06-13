package me.kezer0.landbound.land.database;

import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;

public class databaseManager {

    private static Connection connection;

    public static void initDatabase() {
        try {
            File dataFolder = new File(Bukkit.getPluginsFolder(), "LandBound/players/database");
            if (!dataFolder.exists()) dataFolder.mkdirs();
            File dbFile = new File(dataFolder, "player.db");

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            createTables();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LandBound] Błąd podczas inicjalizacji bazy danych SQLite:");
            e.printStackTrace();
        }
    }

   private static void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS blocks (
                    uuid TEXT NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    z INTEGER NOT NULL,
                    world TEXT NOT NULL,
                    type TEXT NOT NULL,
                    blockData TEXT NOT NULL,
                    customId TEXT,
                    items TEXT,
                    PRIMARY KEY (uuid, x, y, z)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS entities (
                    world TEXT,
                    x INTEGER,
                    y INTEGER,
                    z INTEGER,
                    entityType TEXT,
                    data TEXT,
                    items TEXT,
                    PRIMARY KEY (x, y, z, entityType)
                );
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS items (
                    id TEXT,
                    material TEXT NOT NULL,
                    displayName TEXT,
                    PRIMARY KEY (id, material, displayName)
                );
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    uuid TEXT NOT NULL PRIMARY KEY,
                    level INTEGER NOT NULL DEFAULT 1,
                    health DOUBLE NOT NULL DEFAULT 20.0,
                    damage INTEGER NOT NULL DEFAULT 1
                );
            """);

        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initDatabase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
