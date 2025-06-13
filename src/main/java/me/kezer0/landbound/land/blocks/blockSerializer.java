package me.kezer0.landbound.land.blocks;

import me.kezer0.landbound.utils.signUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class blockSerializer {
    public static blockSaver.BlockData serialize(Block block, Player player, Map<String, blockSaver.BlockData> existingBuffer) {
        Material type = block.getType();
        Location loc = block.getLocation();
        BlockState state = block.getState();
        String customId = null;
        String itemsJson = null;

        // Pomiń górną połówkę np. drzwi
        if (block.getBlockData() instanceof Bisected bisected && bisected.getHalf() == Bisected.Half.TOP) {
            return null;
        }

        // Obsługa signów
        if (state instanceof Sign sign) {
            Component[] lines = sign.getSide(Side.FRONT).lines().toArray(new Component[0]);
            String color = sign.getColor().name();
            boolean glowing = sign.getSide(Side.FRONT).isGlowingText();
            customId = signUtil.serializeSignText(lines, color, glowing);
        }

        // Obsługa item frame
        for (ItemFrame frame : block.getWorld().getNearbyEntitiesByType(ItemFrame.class, loc, 0.5)) {
            if (frame.getLocation().getBlock().equals(block)) {
                ItemStack item = frame.getItem();
                if (!item.getType().isAir()) {
                    itemsJson = blockSerializerHelper.serializeItem(item).toString();
                    break;
                }
            }
        }

        // Obsługa inwentory
        if (state instanceof BlockInventoryHolder holder) {
            ItemStack[] contents = holder.getInventory().getContents();
            itemsJson = blockSerializerHelper.serializeInventory(block, contents, existingBuffer);
        }

        String blockData = state.getBlockData().getAsString();
        return new blockSaver.BlockData(loc, type, blockData, customId, itemsJson);
    }
}
