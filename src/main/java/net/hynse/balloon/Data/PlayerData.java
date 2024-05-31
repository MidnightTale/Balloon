package net.hynse.balloon.Data;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final Map<UUID, UUID> linked = new HashMap<>();
    private final Map<UUID, Integer> balloonCustomModelData = new HashMap<>();
    private final Map<UUID, Boolean> balloonShow = new HashMap<>();
    private final Map<UUID, World> playerWorld = new HashMap<>();

    public UUID getLinked(UUID playerUUID) {
        return linked.get(playerUUID);
    }

    public Integer getBalloonCustomModelData(UUID playerUUID) {
        return balloonCustomModelData.get(playerUUID);
    }

    public Boolean getBalloonShow(UUID playerUUID) {
        return balloonShow.get(playerUUID);
    }

    public World getPlayerWorld(UUID playerUUID) {
        return playerWorld.get(playerUUID);
    }

    public void putLinked(UUID playerId, UUID linkedId) {
        linked.put(playerId, linkedId);
    }

    public void putCustomModelData(UUID playerId, Integer customModelData) {
        balloonCustomModelData.put(playerId, customModelData);
    }

    public void putBalloonShow(UUID playerId, Boolean show) {
        balloonShow.put(playerId, show);
    }
    public void putPlayerWorld(UUID playerId, World world) {
        playerWorld.put(playerId, world);
    }

    public void removeLinked(UUID playerId) {
        linked.remove(playerId);
    }

    public void removeBalloonCustomModelData(UUID playerId) {
        balloonCustomModelData.remove(playerId);
    }

    public void removeBalloonShow(UUID playerId) {
        balloonShow.remove(playerId);
    }
    public void RemovePlayerWorld(UUID playerId) {
        playerWorld.remove(playerId);
    }

    public Map<UUID, UUID> getLinkedMap() {
        return linked;
    }
    public Map<UUID, Integer> getBalloonCustomModelDataMap() {
        return balloonCustomModelData;
    }
    public Map<UUID, Boolean> getBalloonShowMap() {
        return balloonShow;
    }
}
