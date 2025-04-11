package me.kezer0.landbound.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class headCreator {

    public static ItemStack getCustomHead(String base64Texture, String name, List<String> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), "kezer0");
        profile.getProperties().put("textures", new Property("textures", base64Texture));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        meta.setLore(lore);
        meta.setDisplayName(name);
        head.setItemMeta(meta);
        return head;
    }
}
