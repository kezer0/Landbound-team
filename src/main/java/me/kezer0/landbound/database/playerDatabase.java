package me.kezer0.landbound.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class playerDatabase {

    private static final String URL = "jdbc:sqlite:plugins/LandBound/database.db";

    static {
        try {
            createTablesIfNotExist();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private static void createTablesIfNotExist() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String playersTable = "CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY," +
                    "balance INTEGER DEFAULT 0," +
                    "level INTEGER DEFAULT 1," +
                    "experience INTEGER DEFAULT 0" +
                    ");";

            stmt.execute(playersTable);
        }
    }
}