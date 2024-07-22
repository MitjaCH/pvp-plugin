package online.mitja.noElytraNetherite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerTeamDisplay(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        resetPlayerDisplay(player);
    }

    public void updatePlayerTeamDisplay(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getEntryTeam(player.getName());

        if (team != null) {
            ChatColor teamColor = team.getColor();
            String teamName = team.getName();
            String displayName = teamColor + "[" + teamName + "] " + ChatColor.RESET + player.getName();

            player.setDisplayName(displayName);
            player.setPlayerListName(displayName);
            player.setCustomName(displayName);
            player.setCustomNameVisible(true);
        }
    }

    public void resetPlayerDisplay(Player player) {
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        player.setCustomName(player.getName());
        player.setCustomNameVisible(false);
    }
}
