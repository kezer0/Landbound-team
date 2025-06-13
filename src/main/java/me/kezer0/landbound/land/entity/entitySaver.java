package me.kezer0.landbound.land.entity;

import com.google.gson.JsonObject;
import me.kezer0.landbound.land.blocks.blockSaver;
import me.kezer0.landbound.land.database.databaseManager;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class entitySaver {
    private static final List<Entity> entityBuffer = new ArrayList<>();

    public static void queueEntity(Entity entity) {
        entityBuffer.add(entity);
    }

    public static void flushBufferToDisk() {
        for (Entity entity : entityBuffer) {
            saveEntity(entity);
        }
        entityBuffer.clear();
    }

    public static void saveEntity(Entity entity) {
        try (Connection conn = databaseManager.getConnection()) {

            String type = entity.getType().name();
            String world = entity.getWorld().getName();
            int x = entity.getLocation().getBlockX();
            int y = entity.getLocation().getBlockY();
            int z = entity.getLocation().getBlockZ();

            JsonObject data = new JsonObject();
            JsonObject items = new JsonObject();

            switch (type) {
                case "ITEM_FRAME" -> {
                    ItemFrame frame = (ItemFrame) entity;
                    data.addProperty("facing", frame.getAttachedFace().name());
                    data.addProperty("visible", frame.isVisible());
                    data.addProperty("fixed", frame.isFixed());
                    if(frame.getType() == EntityType.GLOW_ITEM_FRAME){
                        data.addProperty("glowing", String.valueOf(EntityType.GLOW_ITEM_FRAME));
                    } else {
                        data.addProperty("glowing", String.valueOf(EntityType.ITEM_FRAME));
                    }
                    frame.getItem();
                    if (!frame.getItem().getType().isAir()) {
                        items = blockSaver.serializeItem(frame.getItem());
                    }
                }
                case "ARMOR_STAND" -> {
                    ArmorStand stand = (ArmorStand) entity;
                    data.addProperty("small", stand.isSmall());
                    data.addProperty("arms", stand.hasArms());
                    data.addProperty("basePlate", stand.hasBasePlate());
                    data.addProperty("visible", stand.isVisible());
                    data.addProperty("invulnerable", stand.isInvulnerable());
                    data.addProperty("gravity", stand.hasGravity());
                    data.addProperty("marker", stand.isMarker());

                    EntityEquipment eq = stand.getEquipment();
                    if (eq.getHelmet() != null && !eq.getHelmet().getType().isAir())
                        items.add("helmet", blockSaver.serializeItem(eq.getHelmet()));
                    if (eq.getChestplate() != null && !eq.getChestplate().getType().isAir())
                        items.add("chestplate", blockSaver.serializeItem(eq.getChestplate()));
                    if (eq.getLeggings() != null && !eq.getLeggings().getType().isAir())
                        items.add("leggings", blockSaver.serializeItem(eq.getLeggings()));
                    if (eq.getBoots() != null && !eq.getBoots().getType().isAir())
                        items.add("boots", blockSaver.serializeItem(eq.getBoots()));
                    eq.getItemInMainHand();
                    if (!eq.getItemInMainHand().getType().isAir())
                        items.add("mainhand", blockSaver.serializeItem(eq.getItemInMainHand()));
                    eq.getItemInOffHand();
                    if (!eq.getItemInOffHand().getType().isAir())
                        items.add("offhand", blockSaver.serializeItem(eq.getItemInOffHand()));
                }
                default -> {
                    return;
                }
            }

            PreparedStatement stmt = conn.prepareStatement(
                "REPLACE INTO entities (world, x, y, z, entityType, data, items) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            stmt.setString(1, world);
            stmt.setInt(2, x);
            stmt.setInt(3, y);
            stmt.setInt(4, z);
            stmt.setString(5, type);
            stmt.setString(6, data.toString());
            stmt.setString(7, items.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
