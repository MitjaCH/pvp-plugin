package online.mitja.noElytraNetherite.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SidebarCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final Map<Player, Boolean> sidebarStatus = new HashMap<>();
    private final Map<String, Integer> teamKills = new HashMap<>();

    public SidebarCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    showSidebar(player);
                    sidebarStatus.put(player, true);
                    player.sendMessage(ChatColor.GREEN + "Sidebar enabled.");
                } else if (args[0].equalsIgnoreCase("off")) {
                    hideSidebar(player);
                    sidebarStatus.put(player, false);
                    player.sendMessage(ChatColor.RED + "Sidebar disabled.");
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /sb <on|off>");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /sb <on|off>");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("on", "off");
        }
        return List.of();
    }

    public void updateSidebarForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (sidebarStatus.getOrDefault(player, true)) {
                showSidebar(player);
            }
        }
    }

    private void showSidebar(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("sidebar", "dummy", ChatColor.GOLD + "Teams and Kills");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = Bukkit.getScoreboardManager().getMainScoreboard().getTeams().size();
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            String teamName = team.getName();
            int kills = teamKills.getOrDefault(teamName, 0);
            Score score = objective.getScore(team.getColor() + teamName + ": " + kills + " kills");
            score.setScore(index--);
        }

        long timeRemaining = (plugin.getConfig().getLong("lockDuration") - (System.currentTimeMillis() - plugin.getConfig().getLong("startTime"))) / 1000;
        int hours = (int) (timeRemaining / 3600);
        int minutes = (int) ((timeRemaining % 3600) / 60);
        int seconds = (int) (timeRemaining % 60);

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        Score timeScore = objective.getScore(ChatColor.YELLOW + "Time until PvP/End: " + timeFormatted);
        timeScore.setScore(0);

        player.setScoreboard(board);
    }

    private void hideSidebar(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void incrementTeamKill(String teamName) {
        teamKills.put(teamName, teamKills.getOrDefault(teamName, 0) + 1);
    }
}
