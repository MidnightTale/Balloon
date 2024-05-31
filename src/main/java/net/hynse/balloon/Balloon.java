package net.hynse.balloon;

import me.nahu.scheduler.wrapper.FoliaWrappedJavaPlugin;
import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import net.hynse.balloon.Command.balloon;
import net.hynse.balloon.Data.PlayerData;
import net.hynse.balloon.Event.PlayerEvent;
import net.hynse.balloon.Gui.ControllerGUI;
import net.hynse.balloon.Gui.ItemGUI;
import net.hynse.balloon.Gui.SelectionGUI;
import net.hynse.balloon.Util.BalloonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.UUID;

public class Balloon extends FoliaWrappedJavaPlugin implements Listener {
    public static Balloon instance;

    public static PlayerData playerData;
    public static BalloonUtil balloonUtil;
    public static ItemGUI itemGUI;
    public static SelectionGUI selectionGUI;

    public Material balloonItem = Material.FEATHER;

    @Override
    public void onEnable() {
        Register();
        scheduleBalloonCleanupTask();
    }

    private void Register() {
        instance = this;
        playerData = new PlayerData();
        balloonUtil = new BalloonUtil();
        itemGUI = new ItemGUI();
        selectionGUI = new SelectionGUI();
        Objects.requireNonNull(getCommand("balloon")).setExecutor(new balloon());
        getServer().getPluginManager().registerEvents(new PlayerEvent(), this);
        getServer().getPluginManager().registerEvents(new ControllerGUI(), this);

        getServer().getPluginManager().registerEvents(this, this);
    }

    private void scheduleBalloonCleanupTask() {
        new WrappedRunnable() {
            @Override
            public void run() {
                if (playerData != null) {
                    for (UUID playerId : playerData.getLinkedMap().keySet()) {
                        UUID parrotId = playerData.getLinked(playerId);
                        Entity entity = Bukkit.getEntity(parrotId);
                        if (entity instanceof Parrot parrot) {
                            if (!parrot.getPassengers().isEmpty()) {
                                Entity passenger = parrot.getPassengers().getFirst();
                                if (passenger instanceof ArmorStand balloon) {
                                    balloon.remove();
                                    parrot.remove();
                                }
                            }
                        }
                        playerData.removeLinked(playerId);
                    }
                } else {
                    getLogger().warning("PlayerData is null, cannot perform balloon cleanup.");
                }
            }
        }.runTaskLater(instance, 10 * 20);
    }
}