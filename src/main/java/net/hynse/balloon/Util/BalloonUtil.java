package net.hynse.balloon.Util;

import org.bukkit.entity.Player;

import java.util.UUID;

import static net.hynse.balloon.Balloon.playerData;
import static org.bukkit.Bukkit.getLogger;

public class SpawnBalloon {

    private void spawnOrUpdateBalloon(Player player, int customModelData) {
        UUID playerId = player.getUniqueId();
        UUID linkedBalloon = playerData.getLinked(playerId);
        if (linkedBalloon != null) {
            getLogger().info(player + "updateBalloon" + customModelData);
            updateBalloon(linkedBalloon, customModelData, player);
        } else {
            getLogger().info(player + "spawnBalloon" + customModelData);
            linkedBalloon = spawnBalloon(player, customModelData);
            playerData.putLinked(playerId,linkedBalloon);
            playerData.putBalloonShow(playerId, true);
            new BalloonFloatTask(linkedBalloon).runTaskTimerAtEntity(this, linkedBalloon, 1L, 4L);
        }
    }

}
