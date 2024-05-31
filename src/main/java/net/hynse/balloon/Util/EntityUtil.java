package net.hynse.balloon.Util;

import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;

public class EntityUtil {
    public void setParrrotComponent(Player player, Parrot parrot) {
        parrot.setTamed(true);
        parrot.setOwner(player);
        parrot.setLeashHolder(player);
        parrot.setSilent(true);
        parrot.setVariant(Parrot.Variant.GRAY);
        parrot.setAdult();
        parrot.setCollidable(false);
    }
}
