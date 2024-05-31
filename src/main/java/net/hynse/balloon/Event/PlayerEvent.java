package net.hynse.balloon.Event;

import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static net.hynse.balloon.Balloon.*;

public class PlayerEvent implements Listener {
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = event.getPlayer().getUniqueId();
        Boolean state = playerData.getBalloonShow(playerId);
        RestoreBalloon(state, player);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RemoveBalloon(player);
    }

    @EventHandler
    private void onPlayerDeathOrRespawn(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        UUID playerId = event.getPlayer().getUniqueId();
        Boolean state = playerData.getBalloonShow(playerId);
        RemoveBalloon(player);
        new WrappedRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && !player.isDead()) {
                    RestoreBalloon(state, player);
                    cancel();
                } else if (!player.isOnline()) {
                    cancel();
                }
            }
        }.runTaskTimerAtEntity(instance, player, 1, 4 * 20);
    }

    private void RestoreBalloon (Boolean state, Player player) {
        UUID playerId = player.getUniqueId();
        if (state != null && state) {
            UUID linkId = playerData.getLinked(playerId);
            Entity linkedEntity = null;
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity.getUniqueId().equals(linkId)) {
                        linkedEntity = entity;
                    }
                }
            if (linkedEntity != null) {
                if (linkedEntity instanceof Parrot parrot) {
                    parrot.teleportAsync(player.getLocation().add(0, 1, 0));
                    parrot.setLeashHolder(player);
                }
            } else {
                int customModelData = playerData.getBalloonCustomModelData(playerId);
                balloonUtil.spawnBalloon(player ,playerId ,customModelData);
            }
        }
    }

    private void RemoveBalloon (Player player) {
        Entity linkedEntity = null;
        UUID playerId = player.getUniqueId();
        UUID linkId = playerData.getLinked(playerId);
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity.getUniqueId().equals(linkId)) {
                linkedEntity = entity;
            }
        }
        if (linkedEntity instanceof Parrot parrot) {
            if (!parrot.getPassengers().isEmpty()) {
                Entity passenger = parrot.getPassengers().getFirst();
                if (passenger instanceof ArmorStand balloon) {
                        balloon.remove();
                        parrot.remove();
                }
            }
        }
    }
}
