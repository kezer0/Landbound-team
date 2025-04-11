package me.kezer0.landbound.commands;

import me.kezer0.landbound.land.configLand;
import me.kezer0.landbound.player.configPlayer;
import me.kezer0.landbound.utils.worldCreator;
import me.kezer0.landbound.utils.worldGenerator;
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
        World world = Bukkit.getWorld(worldName);

        // Jeśli gracz używa /land configLand
        if (args.length == 1 && args[0].equalsIgnoreCase("configLand")) {
            configLand.createorInventory(player);
            return true;
        }

        // Jeśli świat już istnieje, teleportuj
        if (world != null) {
            player.teleport(world.getSpawnLocation());
            player.sendMessage("Teleportowano na Twoją wyspę!");
            return true;
        }

        // Jeśli świat jeszcze nie istnieje – rozpocznij tworzenie
        player.sendMessage("Tworzenie Twojej wyspy, proszę wpisać /land jeszcze raz po chwili...");
        worldCreator.createWorld(player);
        return true;
    }
}
