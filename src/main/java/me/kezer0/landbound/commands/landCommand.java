package me.kezer0.landbound.commands;

import me.kezer0.landbound.land.configLand;
import me.kezer0.landbound.player.configPlayer;
import me.kezer0.landbound.utils.worldCreator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class landCommand implements CommandExecutor {

    private JavaPlugin plugin;
    public landCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko gracze mogą używać tej komendy!");
            return false;
        }
        Player player = (Player) sender;
        String worldName = player.getUniqueId().toString();

        if (args.length == 1 && args[0].equalsIgnoreCase("configLand")) {
            configLand.createorInventory(player);
            return true;
        } else if (args.length == 0) {
            if (Bukkit.getWorld(worldName) != null) {
                World world = Bukkit.getWorld(worldName);
                player.teleport(world.getSpawnLocation());
                player.sendMessage("Teleportowano na Twoją wyspę!");
            } else {
                player.sendMessage("Tworzenie wyspy, proszę czekać...");
                configPlayer cp = new configPlayer(player.getUniqueId());
                worldCreator.createWorld(player).thenAccept(world -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.teleport(world.getSpawnLocation());
                        player.sendMessage("Twoja wyspa została utworzona i jesteś na niej!");
                    });
                });
            }
            return true;
        }
        return false;
    }
}
