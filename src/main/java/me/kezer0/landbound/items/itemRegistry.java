package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class itemRegistry {

    private static final Map<String, ItemStack> idToItemMap = new HashMap<>();

    public static void registerItem(String id, ItemStack item) {
        if (id == null || item == null) return;
        idToItemMap.put(id, item);
    }

    public static ItemStack getItem(String id) {
        ItemStack item = idToItemMap.get(id);
        return item != null ? item.clone() : null;
    }

    public static boolean isRegistered(String id) {
        return idToItemMap.containsKey(id);
    }

    public static Map<String, ItemStack> getAllItems() {
        return new HashMap<>(idToItemMap);
    }
}
