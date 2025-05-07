package me.kezer0.landbound.database;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class itemDatabaseManager {

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS items (" +
                     "id TEXT PRIMARY KEY," +
                     "material TEXT NOT NULL," +
                     "displayName TEXT" +
                     ");";
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveItem(String id, ItemStack item) {
        String sql = "INSERT OR REPLACE INTO items (id, material, displayName) VALUES (?, ?, ?);";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, item.getType().name());
            ItemMeta meta = item.getItemMeta();
            pstmt.setString(3, meta != null ? meta.getDisplayName() : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, ItemStack> loadAllItems() {
        Map<String, ItemStack> items = new HashMap<>();
        String sql = "SELECT id, material, displayName FROM items;";

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                Material material = Material.getMaterial(rs.getString("material"));
                if (material == null) {
                    Bukkit.getLogger().warning("[LandBound] Ostrzeżenie: Nieprawidłowy materiał dla przedmiotu ID: " + id);
                    continue;
                }
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(rs.getString("displayName"));
                    item.setItemMeta(meta);
                }
                items.put(id, item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
