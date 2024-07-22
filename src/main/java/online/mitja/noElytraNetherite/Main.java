package online.mitja.noElytraNetherite;

import online.mitja.noElytraNetherite.commands.SidebarCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {

    private SidebarCommand sidebarCommand;

    @Override
    public void onEnable() {
        getLogger().info("NoElytraNetherite has been enabled!");

        this.saveDefaultConfig();
        if (this.getConfig().getLong("startTime") == 0) {
            this.getConfig().set("startTime", System.currentTimeMillis());
            this.saveConfig();
        }

        String webhookUrl = "YOUR_DISCORD_WEBHOOK_URL";

        getServer().getPluginManager().registerEvents(new EventListener(webhookUrl), this);
        getServer().getPluginManager().registerEvents(new TimeBasedEvents(webhookUrl), this);
        getServer().getPluginManager().registerEvents(this, this);

        sidebarCommand = new SidebarCommand(this);
        if (this.getCommand("sb") != null) {
            this.getCommand("sb").setExecutor(sidebarCommand);
            this.getCommand("sb").setTabCompleter(sidebarCommand);
        } else {
            getLogger().severe("Failed to register /sb command. Check plugin.yml configuration.");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                sidebarCommand.updateSidebarForAllPlayers();
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        getLogger().info("NoElytraNetherite has been disabled.");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            String teamName = event.getEntity().getKiller().getScoreboard().getPlayerTeam(event.getEntity().getKiller()).getName();
            sidebarCommand.incrementTeamKill(teamName);
        }
    }
}