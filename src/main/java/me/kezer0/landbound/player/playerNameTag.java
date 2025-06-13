package me.kezer0.landbound.player;

import me.kezer0.landbound.Landbound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class playerNameTag {

   public static void setPlayerTag(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective(
                Landbound.getInstance().getName().toLowerCase() + "_health",
                Criteria.DUMMY,
                ChatColor.RED + "❤"
        );
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        playerStatistics stats = playerDataManager.getStats(player);
        double health = stats != null ? stats.getHealth() : 20;
        objective.getScore(player.getName()).setScore((int) health);

        player.setScoreboard(board);
    }
}
