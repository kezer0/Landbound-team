package me.kezer0.landbound.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseHelper {

    private static final String DB_URL = "jdbc:sqlite:plugins/LandBound/data.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}