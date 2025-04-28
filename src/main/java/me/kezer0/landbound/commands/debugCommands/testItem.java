package me.kezer0.landbound.commands.debugCommands;

import me.kezer0.landbound.database.itemDatabaseManager;
import me.kezer0.landbound.items.customItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class testItem implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Komenda dostępna tylko dla graczy.");
            return true;
        } else {
            player.getInventory().setItemInMainHand(customItems.createExampleItem());
        }

        return true;
    }
}
