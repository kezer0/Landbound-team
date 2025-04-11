package me.kezer0.landbound.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class hubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("Tylko gracze mogą używać tej komendy!");
            return false;
        }
        Player player = (Player) commandSender;
        World world = Bukkit.getWorld("world");
        Location location = new Location(world, 1,100,1);
        player.teleport(location);

        return true;
    }
}
