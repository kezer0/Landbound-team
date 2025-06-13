package me.kezer0.landbound.commands;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static me.kezer0.landbound.utils.onMob.summonedMob;
import static me.kezer0.landbound.utils.onMob.updateCustomName;

public class summonEntity implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player player)) return false;
        if (args.length < 3 || args.length > 4) {
            player.sendMessage("Użycie: /entity <typ> <health> <damage>");
            return false;
        }
        EntityType entityType = EntityType.valueOf(args[0].toUpperCase());
        try {

            double health = Double.parseDouble(args[1]);
            int damage = Integer.parseInt(args[2]);
            Location loc = player.getLocation();

            LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(loc,entityType);
            entity.registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
             if (entity instanceof Ageable ageable) {
                ageable.setAdult();
            }
             summonedMob(entity,health,damage);
             updateCustomName(entity);
        }catch (IllegalArgumentException e){

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.stream(EntityType.values())
                    .filter(EntityType::isAlive)
                    .map(type -> type.name().toLowerCase())
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
