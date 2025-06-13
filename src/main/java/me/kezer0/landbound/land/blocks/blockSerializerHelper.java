package me.kezer0.landbound.land.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.kezer0.landbound.utils.signUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;

public class blockSerializerHelper {
    public static String serializeLocation(Block block) {
        return block.getX() + "," + block.getY() + "," + block.getZ();
    }

    public static JsonObject serializeItem(ItemStack item) {
        JsonObject obj = new JsonObject();

        String customId = getCustomId(item);
        if (customId != null) {
            obj.addProperty("customId", customId);
        } else {
            obj.addProperty("material", item.getType().name());
        }

        obj.addProperty("amount", item.getAmount());

        if (item.getItemMeta() instanceof BlockStateMeta bsm && bsm.getBlockState() instanceof org.bukkit.block.ShulkerBox shulker) {
            ItemStack[] contents = shulker.getInventory().getContents();
            JsonArray shulkerArray = new JsonArray();

            for (ItemStack shulkerItem : contents) {
                if (shulkerItem != null && !shulkerItem.getType().isAir()) {
                    shulkerArray.add(serializeItem(shulkerItem));
                }
            }

            obj.add("shulkerContents", shulkerArray);
        }

        return obj;
    }

    public static String getCustomId(ItemStack item) {
        if (item.hasItemMeta()) {
            var meta = item.getItemMeta();
            NamespacedKey key = new NamespacedKey("landbound", "custom_id");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            }
        }
        return null;
    }

    public static String serializeInventory(Block block, ItemStack[] contents) {
        JsonArray array = new JsonArray();

        boolean isDoubleChest = false;
        boolean isLeftSide = false;
        String doubleChestId = null;

        if (block.getState() instanceof Chest chest) {
            InventoryHolder holder = chest.getInventory().getHolder();
            if (holder instanceof DoubleChest doubleChest) {
                isDoubleChest = true;
                Chest leftChest = (Chest) doubleChest.getLeftSide();
                Block leftBlock = leftChest.getBlock();
                isLeftSide = block.getLocation().equals(leftBlock.getLocation());
                doubleChestId = serializeLocation(leftBlock);
                Bukkit.getLogger().info("Serializing inventory for block at " + block.getLocation());
            } else {
                Bukkit.getLogger().info("Single chest at " + block.getLocation());
            }
        }

        if (isDoubleChest && !isLeftSide) {
            return null;
        }

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item == null || item.getType().isAir()) continue;

            JsonObject obj = serializeItem(item);
            obj.addProperty("slot", slot);

            if (isDoubleChest) {
                obj.addProperty("isDouble", true);
                obj.addProperty("isLeftSide", isLeftSide);
                obj.addProperty("doubleChestId", doubleChestId);
            }

            array.add(obj);
        }

        return array.toString();
    }

    public static boolean isTopHalfBisected(Block block) {
        if (block.getBlockData() instanceof Bisected bisected) {
            return bisected.getHalf() == Bisected.Half.TOP;
        }
        return false;
    }

    public static String serializeSignIfPresent(Block block) {
        BlockState state = block.getState();
        if (state instanceof Sign sign) {
            var frontLines = sign.getSide(Side.FRONT).lines().toArray(new net.kyori.adventure.text.Component[0]);
            String color = sign.getColor().name();
            boolean glowing = sign.getSide(Side.FRONT).isGlowingText();
            return signUtil.serializeSignText(frontLines, color, glowing);
        }
        return null;
    }

    public static String serializeItemFromFrameIfPresent(Block block) {
        Collection<ItemFrame> frames = block.getLocation().getWorld().getNearbyEntitiesByType(ItemFrame.class, block.getLocation(), 0.5);
        for (ItemFrame frame : frames) {
            if (frame.getLocation().getBlock().equals(block)) {
                ItemStack item = frame.getItem();
                if (!item.getType().isAir()) {
                    return serializeItem(item).toString();
                }
            }
        }
        return null;
    }

    public static boolean hasInventoryItems(BlockState state) {
        if (state instanceof BlockInventoryHolder holder) {
            for (ItemStack item : holder.getInventory().getContents()) {
                if (item != null && !item.getType().isAir()) {
                    return true;
                }
            }
        }
        return false;
    }
}
