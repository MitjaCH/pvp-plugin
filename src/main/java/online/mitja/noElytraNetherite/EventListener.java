package online.mitja.noElytraNetherite;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class EventListener implements Listener {

    private Set<String> notifiedPlayers = new HashSet<>();
    private final DiscordWebhook webhook;

    public EventListener(String webhookUrl) {
        this.webhook = new DiscordWebhook(webhookUrl);
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getItemStack().getType() == Material.ELYTRA || event.getItem().getItemStack().getType().toString().contains("NETHERITE")) {
            event.setCancelled(true);
            event.getItem().remove();
            if (notifiedPlayers.add(player.getName())) {
                player.sendMessage("Elytra and Netherite items are disabled on this server.");
            }
            webhook.sendMessage("Server Alert", player.getName() + " tried to pick up a disabled item: " + event.getItem().getItemStack().getType(), null);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && (event.getCurrentItem().getType() == Material.ELYTRA || event.getCurrentItem().getType().toString().contains("NETHERITE"))) {
            event.setCancelled(true);
            event.getWhoClicked().getInventory().remove(event.getCurrentItem());
            if (notifiedPlayers.add(event.getWhoClicked().getName())) {
                ((Player) event.getWhoClicked()).sendMessage("Elytra and Netherite items are disabled on this server.");
            }
            webhook.sendMessage("Server Alert", event.getWhoClicked().getName() + " tried to move a disabled item: " + event.getCurrentItem().getType(), null);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getItem().getType() == Material.ELYTRA || event.getItem().getType().toString().contains("NETHERITE")) {
            event.setCancelled(true);
            event.getSource().remove(event.getItem());
            webhook.sendMessage("Server Alert", "An attempt was made to move a disabled item: " + event.getItem().getType(), null);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to the server! Elytra and Netherite items are disabled.");
    }
}