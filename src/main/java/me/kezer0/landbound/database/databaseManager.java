package me.kezer0.landbound.database;

import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;

public class databaseManager {
    private static Connection connection;

    public static void initDatabase() {
        try {
            File dataFolder = new File(Bukkit.getPluginsFolder(), "LandBound/database");
            if (!dataFolder.exists()) dataFolder.mkdirs();
            File dbFile = new File(dataFolder, "blocks.db");

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            createTables();
            alterTablesIfNeeded();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LandBound] Błąd podczas inicjalizacji bazy danych SQLite:");
            e.printStackTrace();
        }
        Bukkit.getLogger().info("[LandBound] Połączono z bazą danych SQLite!");
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
        }
    }

    private static void alterTablesIfNeeded() {
        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery("PRAGMA table_info(blocks);");
            boolean hasItemsColumn = false;

            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("items".equalsIgnoreCase(columnName)) {
                    hasItemsColumn = true;
                    break;
                }
            }
            rs.close();

            if (!hasItemsColumn) {
                Bukkit.getLogger().info("[LandBound] Dodawanie brakującej kolumny 'items' do tabeli 'blocks'...");
                stmt.execute("ALTER TABLE blocks ADD COLUMN items TEXT;");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LandBound] Błąd podczas sprawdzania lub dodawania kolumny 'items':");
            e.printStackTrace();
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
