package me.kezer0.landbound.land.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.kezer0.landbound.land.blocks.blockReconstructor;
import me.kezer0.landbound.land.database.databaseManager;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class entityReconstructor {

    private static final NamespacedKey ENTITY_TAG = new NamespacedKey("landbound", "custom_entity");
    public static boolean isLoadingEntities = false;

    public static void loadAllEntities() {
        isLoadingEntities = true;
        try (Connection conn = databaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM entities");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String worldName = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String entityType = rs.getString("entityType");
                String dataJson = rs.getString("data");
                String itemJson = rs.getString("items");

                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;

                Location loc = new Location(world, x + 0.5, y + 0.5, z + 0.5);

                switch (entityType) {
                    case "ITEM_FRAME" -> reconstructItemFrame(loc, dataJson, itemJson);
                    case "ARMOR_STAND" -> reconstructArmorStand(loc, dataJson, itemJson);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isLoadingEntities = false;
        }
    }


    private static void reconstructItemFrame(Location frameLoc, String dataJson, String itemJson) {
        try {
            JsonObject data = JsonParser.parseString(dataJson).getAsJsonObject();
            BlockFace facing = BlockFace.valueOf(data.get("facing").getAsString());
            boolean visible = data.get("visible").getAsBoolean();
            boolean fixed = data.get("fixed").getAsBoolean();
            EntityType entityType = EntityType.valueOf(data.get("glowing").getAsString());

            worldCleanup(frameLoc, ItemFrame.class);

            ItemFrame frame = (ItemFrame) frameLoc.getWorld().spawnEntity(frameLoc, entityType);
            frame.setFacingDirection(facing.getOppositeFace(), true);
            frame.setVisible(visible);
            frame.setFixed(fixed);

            if (itemJson != null) {
                JsonObject obj = JsonParser.parseString(itemJson).getAsJsonObject();
                ItemStack item = blockReconstructor.deserializeItem(obj);
                frame.setItem(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void reconstructArmorStand(Location loc, String dataJson, String itemJson) {
        try {
            JsonObject data = JsonParser.parseString(dataJson).getAsJsonObject();

            worldCleanup(loc, ArmorStand.class);
            ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            stand.getPersistentDataContainer().set(ENTITY_TAG, PersistentDataType.BYTE, (byte) 1);

            if (data.has("small")) stand.setSmall(data.get("small").getAsBoolean());
            if (data.has("arms")) stand.setArms(data.get("arms").getAsBoolean());
            if (data.has("basePlate")) stand.setBasePlate(data.get("basePlate").getAsBoolean());
            if (data.has("visible")) stand.setVisible(data.get("visible").getAsBoolean());
            if (data.has("invulnerable")) stand.setInvulnerable(data.get("invulnerable").getAsBoolean());
            if (data.has("gravity")) stand.setGravity(data.get("gravity").getAsBoolean());
            if (data.has("marker")) stand.setMarker(data.get("marker").getAsBoolean());

            if (itemJson != null && !itemJson.isEmpty()) {
                JsonObject items = JsonParser.parseString(itemJson).getAsJsonObject();
                EntityEquipment eq = stand.getEquipment();

                if (items.has("helmet"))
                    eq.setHelmet(blockReconstructor.deserializeItem(items.get("helmet").getAsJsonObject()));
                if (items.has("chestplate"))
                    eq.setChestplate(blockReconstructor.deserializeItem(items.get("chestplate").getAsJsonObject()));
                if (items.has("leggings"))
                    eq.setLeggings(blockReconstructor.deserializeItem(items.get("leggings").getAsJsonObject()));
                if (items.has("boots"))
                    eq.setBoots(blockReconstructor.deserializeItem(items.get("boots").getAsJsonObject()));
                if (items.has("mainHand"))
                    eq.setItemInMainHand(blockReconstructor.deserializeItem(items.get("mainHand").getAsJsonObject()));
                else if (items.has("mainhand"))
                    eq.setItemInMainHand(blockReconstructor.deserializeItem(items.get("mainhand").getAsJsonObject()));
                if (items.has("offHand"))
                    eq.setItemInOffHand(blockReconstructor.deserializeItem(items.get("offHand").getAsJsonObject()));
                else if (items.has("offhand"))
                    eq.setItemInOffHand(blockReconstructor.deserializeItem(items.get("offhand").getAsJsonObject()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void worldCleanup(Location loc, Class<? extends Entity> entityType) {
        loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5).forEach(entity -> {
            if (entityType.isInstance(entity)) {
                entity.remove();
            }
        });
    }
}
