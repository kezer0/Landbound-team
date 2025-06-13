package me.kezer0.landbound.land.entity;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import me.kezer0.landbound.land.database.databaseManager;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class entityDataListener implements Listener {

    @EventHandler
    public void onEntityAdd(EntityAddToWorldEvent e) {
        if (entityReconstructor.isLoadingEntities) return;

        Entity entity = e.getEntity();
        if(!(entity.getWorld().getName().startsWith("island_"))) return;
        if (entity instanceof ItemFrame || entity instanceof ArmorStand || entity instanceof Painting) {
            entitySaver.queueEntity(entity);
        }
}

    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent e) {
        Entity entity = e.getEntity();
        if(!(entity.getWorld().getName().startsWith("island_"))) return;
        if(!(entity instanceof ItemFrame || entity instanceof ArmorStand || entity instanceof Painting)) return;

        Location loc = entity.getLocation();
        String world = loc.getWorld().getName();

        try (Connection conn = databaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM entities WHERE x = ? AND y = ? AND z = ? AND world = ? AND entityType = ?"
            );
            stmt.setInt(1, loc.getBlockX());
            stmt.setInt(2, loc.getBlockY());
            stmt.setInt(3, loc.getBlockZ());
            stmt.setString(4, world);
            stmt.setString(5, entity.getType().name());
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        entitySaver.flushBufferToDisk();
    }

}
