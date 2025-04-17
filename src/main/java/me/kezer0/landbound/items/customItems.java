package me.kezer0.landbound.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class customItems {

    private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(customItems.class);

    public static List<ItemStack> getAllCustomItems() {
        return Arrays.asList(createExampleItem());
    }

    private static ItemStack createExampleItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        String displayName = "§bExcalibur";
        meta.setDisplayName(displayName);

        // Generujemy i przypisujemy ID do persistentDataContainer
        String id = generateId(item, displayName);
        NamespacedKey key = new NamespacedKey(plugin, "custom_id");
        meta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.STRING, id);

        item.setItemMeta(meta);
        return item;
    }

    public static void registerAll() {
        for (ItemStack item : getAllCustomItems()) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            NamespacedKey key = new NamespacedKey(plugin, "custom_id");
            String id = meta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.STRING);
            if (id != null) {
                itemRegistry.registerItem(id, item);
            }
        }
    }

    private static String generateId(ItemStack item, String displayName) {
        String base = item.getType().name();
        String name = displayName != null ? displayName.replaceAll("§.", "").replace(" ", "_") : "default";
        return base.toLowerCase() + "_" + name.toLowerCase();
    }
}
