package me.kezer0.landbound.items;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class itemRegistry {

    private static final Map<String, ItemStack> idToItemMap = new HashMap<>();

    public static void registerItem(String id, ItemStack item) {
        if (id == null || item == null) return;
        idToItemMap.put(id.toLowerCase(), item.clone());
    }

    public static ItemStack getItem(String id) {
        if (id == null) return null;
        ItemStack item = idToItemMap.get(id.toLowerCase());
        return item != null ? item.clone() : null;
    }

    public static boolean isRegistered(String id) {
        return idToItemMap.containsKey(id.toLowerCase());
    }

    public static Map<String, ItemStack> getAllItems() {
        return Collections.unmodifiableMap(idToItemMap);
    }
}