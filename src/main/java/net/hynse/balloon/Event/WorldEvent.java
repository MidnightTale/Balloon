package net.hynse.balloon.Event;

import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import net.hynse.balloon.Balloon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.hynse.balloon.Balloon.playerData;
import static net.hynse.balloon.Balloon.playerEvent;

public class WorldEvent implements Listener {
    private static final Map<UUID, String> playerWorldMap = new HashMap<>();

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String fromWorld = event.getFrom().getName();
        String toWorld = player.getWorld().getName();

        UUID playerId = player.getUniqueId();
        String previousWorld = playerWorldMap.get(playerId);

        if (previousWorld != null && !previousWorld.equals(toWorld)) {
            onPlayerChangedWorld(player, fromWorld, toWorld);
        }

        playerWorldMap.put(playerId, toWorld);
    }

    private void onPlayerChangedWorld(Player player, String fromWorld, String toWorld) {
        boolean state = playerData.getBalloonShow(player.getUniqueId());
        if (state) {
            new WrappedRunnable() {
                @Override
                public void run() {
                    playerEvent.RemoveBalloon(player);
                    playerEvent.RestoreBalloon(state, player);
                }
            }.runTaskAtEntity(Balloon.instance, player);
        }
    }

    public static class WorldChangeTask extends WrappedRunnable {
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                String currentWorld = player.getWorld().getName();
                String previousWorld = playerWorldMap.get(playerId);

                if (previousWorld != null && !previousWorld.equals(currentWorld)) {
                    // Call the method in the outer class
                    WorldEvent worldEvent = new WorldEvent();
                    worldEvent.onPlayerChangedWorld(player, previousWorld, currentWorld);
                }

                playerWorldMap.put(playerId, currentWorld);
            }
        }
    }
}
