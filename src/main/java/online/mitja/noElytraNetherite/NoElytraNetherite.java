package online.mitja.noElytraNetherite;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

public final class NoElytraNetherite extends JavaPlugin {

    String webhookUrl = "https://discord.com/api/webhooks/1264665160614678529/7QAssZx_qXyQT3M3tGfkMbQMVDYZ8RZJwx7IuuMGnC5Jqur9hCiwBza59ZygweIvU1gn";

    @Override
    public void onEnable() {
        getLogger().info("NoElytraNetherite has been enabled!");
        getServer().getPluginManager().registerEvents(new EventListener(webhookUrl), this);
        getServer().getPluginManager().registerEvents(new TimeBasedEvents(webhookUrl), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("NoElytraNetherite has been disabled!");
    }
}
