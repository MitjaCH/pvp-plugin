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
        Objective objective = board.registerNewObjective("sidebar", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "§l§nServer Info");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = 15;

        // Title Header
        Score header = objective.getScore(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Teams and Kills");
        header.setScore(index--);

        // Teams and Kills
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            String teamName = team.getName();
            int kills = teamKills.getOrDefault(teamName, 0);
            Score score = objective.getScore(team.getColor() + teamName + ": " + ChatColor.WHITE + kills + " kills");
            score.setScore(index--);
        }

        // Spacing
        Score separator1 = objective.getScore(ChatColor.GRAY + "---------------");
        separator1.setScore(index--);

        // PvP/End Time Remaining
        long timeRemaining = (plugin.getConfig().getLong("lockDuration") - (System.currentTimeMillis() - plugin.getConfig().getLong("startTime"))) / 1000;
        int hours = (int) (timeRemaining / 3600);
        int minutes = (int) ((timeRemaining % 3600) / 60);
        int seconds = (int) (timeRemaining % 60);

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        Score pvpInfo = objective.getScore(ChatColor.YELLOW + "PvP & End restrictions");
        pvpInfo.setScore(index--);
        Score timeScore = objective.getScore(ChatColor.YELLOW + "disabled in: " + ChatColor.WHITE + timeFormatted);
        timeScore.setScore(index--);

        // More Spacing
        Score separator2 = objective.getScore(ChatColor.GRAY + "---------------");
        separator2.setScore(index--);

        // Player Count
        Score playerCount = objective.getScore(ChatColor.GREEN + "Online Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
        playerCount.setScore(index--);

        // More Information
        Score serverMOTD = objective.getScore(ChatColor.GOLD + "MOTD: " + ChatColor.WHITE + Bukkit.getMotd());
        serverMOTD.setScore(index--);

        // More Spacing
        Score separator3 = objective.getScore(ChatColor.GRAY + "---------------");
        separator3.setScore(index--);

        // Server IP
        Score footer1 = objective.getScore(ChatColor.AQUA + "Server IP:");
        footer1.setScore(index--);
        Score footer2 = objective.getScore(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "mc.mitja.online");
        footer2.setScore(index--);

        player.setScoreboard(board);
    }

    private void hideSidebar(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void incrementTeamKill(String teamName) {
        teamKills.put(teamName, teamKills.getOrDefault(teamName, 0) + 1);
    }
}
