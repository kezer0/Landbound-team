package me.kezer0.landbound.commands.hub;

import me.kezer0.landbound.player.playerDataManager;
import me.kezer0.landbound.land.generation.worldDataGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class hubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("Tylko gracze mogą używać tej komendy!");
            return false;
        }
        Player player = (Player) commandSender;
        World world = Bukkit.getWorld("world");
        File playersRootFolder = playerDataManager.getPlayersRootFolder();
        File islandFile = new File(playersRootFolder, player.getUniqueId() + "/island.yml");
        worldDataGenerator generator = new worldDataGenerator(player, islandFile);
        Location location = new Location(world, 1,100,1);
        generator.saveToConfig();
        player.teleport(location);

        return true;
    }
}
