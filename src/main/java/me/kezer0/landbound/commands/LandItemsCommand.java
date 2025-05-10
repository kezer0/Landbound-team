package me.kezer0.landbound.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LandItemsCommand implements CommandExecutor, TabCompleter {

    private final List<String> items = Arrays.asList(
        "PhoenixCrown",
        "EnderBackpack",
        "MagicAxe",
        "MythicSword",
        "ObsidianSword",
        "FrostMace",
        "MysteriousHelmet",
        "LifePotion",
        "DragonArmor",
        "DragonPickaxe",
        "ThunderAxe",
        "UraniumDagger"
    );

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return false;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Nie masz uprawnień żeby używać tej komendy!");
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Użyj: /dev <nazwaitemu>");
            return false;
        }

        String requestedItem = args[0];
        if (!items.contains(requestedItem)) {
            player.sendMessage(ChatColor.RED + "Nieprawidłowy przedmiot! Dostępne przedmioty: " +
                ChatColor.YELLOW + String.join(", ", items));
            return false;
        }

        giveCustomItem(player, requestedItem);
        player.sendMessage(ChatColor.GREEN + "Przedmiot otrzymany: " + ChatColor.GOLD + requestedItem);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partialItem = args[0].toLowerCase();
            for (String item : items) {
                if (item.toLowerCase().startsWith(partialItem)) {
                    completions.add(item);
                }
            }
        }

        return completions;
    }

    private void giveCustomItem(Player player, String itemName) {
        ItemStack item = null;
        ItemMeta meta = null;

        switch (itemName) {
            case "PhoenixCrown":
                item = new ItemStack(Material.GOLDEN_HELMET);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GOLD + "KORONA FENIKSA");
                break;

            case "EnderBackpack":
                item = new ItemStack(Material.ENDER_CHEST);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "PLECAK KRESU");
                break;

            case "MagicAxe":
                item = new ItemStack(Material.DIAMOND_AXE);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "MAGICZNA SIEKIERKA");
                break;

            case "MythicSword":
                item = new ItemStack(Material.NETHERITE_SWORD);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "MITYCZNY MIECZ");
                break;

            case "ObsidianSword":
                item = new ItemStack(Material.NETHERITE_SWORD);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "OBSYDIANOWY MIECZ");
                break;

            case "FrostMace":
                item = new ItemStack(Material.MACE); 
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "LODOWY TOPÓR");
                break;

            case "MysteriousHelmet":
                item = new ItemStack(Material.NETHERITE_HELMET);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "TAJEMNICZY HEŁM");
                break;

            case "DragonArmor":
                item = new ItemStack(Material.NETHERITE_CHESTPLATE);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SMOCZY PANCERZ");
                break;

            case "DragonPickaxe":
                item = new ItemStack(Material.NETHERITE_PICKAXE);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SMOCZY KILOF");
                break;

            case "ThunderAxe":
                item = new ItemStack(Material.NETHERITE_AXE);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.GOLD + "SIEKIERKA ZEUSA");
                break;

            default:
                player.sendMessage(ChatColor.RED + "Nie można utworzyć tego przedmiotu!");
                return;
        }

        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Specjalny przedmiot z komendy /dev");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        if (item != null) {
            player.getInventory().addItem(item);
        }
    }
}