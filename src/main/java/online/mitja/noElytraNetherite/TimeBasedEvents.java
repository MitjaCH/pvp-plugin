package online.mitja.noElytraNetherite;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.entity.Player;

public class TimeBasedEvents implements Listener {

    private final long lockDuration = 3 * 24 * 60 * 60 * 1000L; // 3 days in milliseconds
    private final long startTime;
    private final DiscordWebhook webhook;

    public TimeBasedEvents(String webhookUrl) {
        this.startTime = System.currentTimeMillis();
        this.webhook = new DiscordWebhook(webhookUrl);
    }

    @EventHandler
    public void onPlayerUseEndPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerPortalEvent.TeleportCause.END_PORTAL && System.currentTimeMillis() - startTime < lockDuration) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("The End is locked for the first 3 days.");
            webhook.sendMessage("Server Alert", event.getPlayer().getName() + " tried to use an End portal.", null);
        }
    }

    @EventHandler
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        if (System.currentTimeMillis() - startTime < lockDuration && event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            event.setCancelled(true);
            ((Player) event.getDamager()).sendMessage("PvP is disabled for the first 3 days.");
            webhook.sendMessage("Server Alert", event.getDamager().getName() + " tried to engage in PvP.", null);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.END_PORTAL_FRAME) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You cannot activate the End portal.");
            webhook.sendMessage("Server Alert", event.getPlayer().getName() + " tried to place an Ender Eye in a portal frame.", null);
        }
    }
}