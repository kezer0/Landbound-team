package me.kezer0.landbound.land.generation;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.TreeType;

import java.util.*;

public class generateEnvironment implements Listener {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    private final Map<Chunk, Integer> treesPerChunk = new HashMap<>();
    private final int MAX_TREES_PER_CHUNK = 5;

    public generateEnvironment(JavaPlugin plugin) {
        this.plugin = plugin;
        startTreeGenerationTask();
    }

    private void startTreeGenerationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Player player : Bukkit.getOnlinePlayers()) {

                    World world = player.getWorld();
                    String worldName = world.getName();
                    String expectedWorldName = player.getUniqueId().toString();

                    if (!worldName.equals("island_" + expectedWorldName)) continue;

                    Bukkit.getLogger().info("Gracz " + player.getName() + " jest na swoim świecie: " + worldName);
                    Chunk chunk = player.getLocation().getChunk();
                    treesPerChunk.putIfAbsent(chunk, 0);
                    int existing = treesPerChunk.get(chunk);

                    if (existing >= MAX_TREES_PER_CHUNK) continue;

                    int toGenerate = Math.min(4, MAX_TREES_PER_CHUNK - existing);
                    int howMany = random.nextInt(toGenerate) + 1;
                    Bukkit.getLogger().info("Postawiony " + howMany);

                    for (int i = 0; i < howMany; i++) {

                        Location base = getValidTreeSpot(chunk);
                        if (base == null) {
                            Bukkit.getLogger().info("Nie znaleziono odpowiedniego bloku do postawienia drzewa.");
                            return;
                        } else if (generateSafeTree(base, TreeType.TREE)) {
                            Bukkit.getLogger().info("Location = " + base + ", Type = " + TreeType.TREE);
                            Bukkit.getLogger().info("RÓŻNICA");
                            Bukkit.getLogger().info("generateSafeTree zwrócił false dla lokalizacji: " + base);
                        }
                        world.generateTree(base,TreeType.TREE);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 30 * 1);  // 4min
    }

    private Location getValidTreeSpot(Chunk chunk) {
        World world = chunk.getWorld();
        int startX = chunk.getX() << 4;
        int startZ = chunk.getZ() << 4;

        for (int attempt = 0; attempt < 20; attempt++) {
            int x = startX + random.nextInt(16);
            int z = startZ + random.nextInt(16);
            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x, y, z);

            Block soil = loc.clone().subtract(0, 1, 0).getBlock();
            if (isValidSoil(soil) && isSpaceAboveClear(loc, 9) && isLeafAreaClear(loc, 9, 2)) {
                return loc;
            }
        }
        return null;
    }

    private boolean generateSafeTree(Location loc, TreeType type) {
         Bukkit.getLogger().info("Location = " + loc + ", Type = " + type);
        return loc.getWorld().generateTree(loc, type);
    }

    private boolean isValidSoil(Block soil) {
        Material type = soil.getType();
        Bukkit.getLogger().info("isValidSoil");
        return type == Material.DIRT || type == Material.GRASS_BLOCK || type == Material.PODZOL;
    }

    private boolean isSpaceAboveClear(Location loc, int height) {
        for (int y = 1; y <= height; y++) {
            Block b = loc.clone().add(0, y, 0).getBlock();
            if (!b.isEmpty()) return false;
        }
         Bukkit.getLogger().info("spaceAbove is valid");
        return true;
    }

    private boolean isLeafAreaClear(Location base, int height, int radius) {
        for (int y = 5; y <= height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = base.clone().add(x, y, z).getBlock();
                    if (!b.isEmpty() && !b.getType().name().contains("LEAVES")) {
                        return false;
                    }
                }
            }
        }
        Bukkit.getLogger().info("space for leaves are valid");
        return true;
    }
}
