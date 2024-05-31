package net.hynse.balloon.Event;

import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import net.hynse.balloon.Balloon;
import net.hynse.balloon.Util.BalloonUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

import static net.hynse.balloon.Balloon.balloonUtil;
import static net.hynse.balloon.Balloon.playerData;


public class PlayerEvent implements Listener {
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = event.getPlayer().getUniqueId();
        Boolean state = playerData.getBalloonShow(playerId);
        RestoreBalloon(state, player);

        new WrappedRunnable() {
            @Override
            public void run() {
                player.getWorld().getNearbyEntities(player.getLocation(), 16, 16, 16).forEach(entity -> {
                    if (entity instanceof Parrot parrot) {
                        AnimalTamer owner = parrot.getOwner();
                        if (owner != null && !((OfflinePlayer) owner).isOnline() && Boolean.TRUE.equals(parrot.getPersistentDataContainer().get(Balloon.instance.balloonCleanUpKey, PersistentDataType.BOOLEAN))) {
                            parrot.getPassengers().forEach(Entity::remove);
                            playerData.removeLinked(player.getUniqueId());
                            parrot.setLeashHolder(null);
                            parrot.remove();
                        }
                    }
                });
            }
        }.runTaskAtLocation(Balloon.instance, player.getLocation());
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
        }.runTaskTimerAtEntity(Balloon.instance, player, 1, 4 * 20);
    }

//    @EventHandler
//    public void onPlayerTeleport(PlayerTrackEntityEvent event) {
//        Balloon.instance.getLogger().info("onPlayerTeleport");
//        Entity entity = event.getEntity();
//        if (entity instanceof Player player) {
//            Balloon.instance.getLogger().info("onPlayerTeleport found player");
//            UUID playerId = player.getUniqueId();
//            if (playerData.getBalloonShow(playerId) != null) {
//                Balloon.instance.getLogger().info("onPlayerTeleport state is not null");
//                boolean isShow = playerData.getBalloonShow(playerId);
//                int customModelData = playerData.getBalloonCustomModelData(playerId);
//                if (isShow) {
//                    Balloon.instance.getLogger().info("onPlayerTeleport state is on");
//                    RemoveBalloon(player);
//                    balloonUtil.spawnOrUpdateBalloon(player, customModelData);
//                }
//            }
//        }
//    }


    public void RestoreBalloon(Boolean state, Player player) {
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
                    new BalloonUtil.BalloonFloatTask(parrot).runTaskTimerAtEntity(Balloon.instance, parrot, 1L, 4L);
                }
            } else {
                int customModelData = playerData.getBalloonCustomModelData(playerId);
                balloonUtil.spawnBalloon(player ,playerId ,customModelData);
            }
        }
    }

    public void RemoveBalloon(Player player) {
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
                        playerData.removeLinked(playerId);
                }
            }
        }
    }
}
