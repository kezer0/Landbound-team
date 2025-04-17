package me.kezer0.landbound.blocks;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class blockReconstructor {

    public static void loadBlocks(Player player) {
        UUID uuid = player.getUniqueId();
        File file = blockDataManager.getBlockDataFile(uuid);

        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Set<String> keys = config.getKeys(false);

        for (String key : keys) {
            String typeString = config.getString(key + ".type");
            String dataString = config.getString(key + ".data");
            String worldName = config.getString(key + ".world");
            String customId = config.getString(key + ".custom_id");

            if (typeString == null || dataString == null || worldName == null) {
                Bukkit.getLogger().warning("Brak wymaganych danych przy odczycie bloku: " + key);
                continue;
            }

            String[] coords = key.split(",");
            if (coords.length != 3) continue;

            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int z = Integer.parseInt(coords[2]);

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                Bukkit.getLogger().warning("Świat nie znaleziony: " + worldName);
                continue;
            }

            Block block = world.getBlockAt(x, y, z);
            Material material = Material.getMaterial(typeString);
            if (material == null) {
                Bukkit.getLogger().warning("Nieprawidłowy typ bloku: " + typeString);
                continue;
            }

            block.setType(material, false);
            try {
                block.setBlockData(Bukkit.createBlockData(dataString), false);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Nie udało się ustawić danych bloku: " + dataString + " dla " + key);
                continue;
            }
            if (customId != null && customId.startsWith("SIGN:") && block.getState() instanceof Sign sign) {
                String content = customId.substring("SIGN:".length());

                // Podział treści i parametrów
                String[] mainParts = content.split(";color=");
                String[] lines = mainParts[0].split(";;");
                String color = "WHITE";
                boolean glowing = false;

                if (mainParts.length > 1) {
                    String[] attrs = mainParts[1].split(";");
                    color = attrs[0];
                    if (attrs.length > 1 && attrs[1].startsWith("glowing=")) {
                        glowing = Boolean.parseBoolean(attrs[1].substring("glowing=".length()));
                    }
                }

                net.kyori.adventure.text.Component[] components = new net.kyori.adventure.text.Component[4];
                for (int i = 0; i < 4; i++) {
                    components[i] = i < lines.length ? net.kyori.adventure.text.Component.text(lines[i]) : net.kyori.adventure.text.Component.empty();
                }
                sign.setGlowingText(glowing);
                try {
                    sign.setColor(DyeColor.valueOf(color));
                } catch (IllegalArgumentException ignored) {
                }

                sign.update(true, false);
            }
        }
    }
}
