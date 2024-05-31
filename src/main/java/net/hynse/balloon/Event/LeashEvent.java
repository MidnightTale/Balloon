package net.hynse.balloon.Event;

import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import net.hynse.balloon.Balloon;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import static net.hynse.balloon.Balloon.playerData;

public class LeashEvent implements Listener {

    @EventHandler
    public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Parrot parrot) {
            if (parrot.getPersistentDataContainer().has(Balloon.instance.balloonCleanUpKey, PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onPlayerleashEntity(PlayerLeashEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Parrot parrot) {
            if (parrot.getPersistentDataContainer().has(Balloon.instance.balloonCleanUpKey, PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
            }
        }
    }
}
