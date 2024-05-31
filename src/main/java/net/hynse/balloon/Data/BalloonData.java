package net.hynse.balloon.Data;

import java.util.UUID;

public class BalloonData {
    private UUID playerUUID;
    private UUID entityUUID;
    private boolean isShow;
    private String balloonType;

    public void Object(UUID playerUUID, UUID entityUUID, boolean isShow, String balloonType) {
        this.playerUUID = playerUUID;
        this.entityUUID = entityUUID;
        this.isShow = isShow;
        this.balloonType = balloonType;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public void setEntityUUID(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getBalloonType() {
        return balloonType;
    }

    public void setBalloonType(String balloonType) {
        this.balloonType = balloonType;
    }
}
