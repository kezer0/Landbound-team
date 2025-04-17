package me.kezer0.landbound.blocks;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class blockDataManager {

    private static final File baseFolder = new File(Bukkit.getPluginsFolder(), "LandBound/players");

    public static File getBlockDataFile(UUID uuid) {
        File playerFolder = new File(baseFolder, uuid.toString());
        File blockDataFile = new File(playerFolder, "blocks.yml");

        if (!blockDataFile.exists()) {
            try {
                if (!playerFolder.exists()) playerFolder.mkdirs();
                blockDataFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("[LandBound] Błąd przy tworzeniu pliku blocks.yml dla gracza: " + uuid);
                e.printStackTrace();
            }
        }

        return blockDataFile;
    }
}
