package me.kezer0.landbound.commands.debugCommands;

import me.kezer0.landbound.database.itemDatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class debugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Komenda dostępna tylko dla graczy.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Użycie: /debugitem <id>");
            return true;
        }

        String id = args[0];
        Map<String, ItemStack> items = itemDatabaseManager.loadAllItems();
        ItemStack item = items.get(id);

        if (item != null) {
            player.getInventory().addItem(item);
            sender.sendMessage("Przedmiot o ID '" + id + "' został dodany do ekwipunku.");
        } else {
            sender.sendMessage("Nie znaleziono przedmiotu o ID '" + id + "'.");
        }

        return true;
    }
}
