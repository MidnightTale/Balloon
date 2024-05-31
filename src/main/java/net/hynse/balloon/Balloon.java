package net.hynse.balloon;

import me.nahu.scheduler.wrapper.FoliaWrappedJavaPlugin;
import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import net.hynse.balloon.Command.balloon;
import net.hynse.balloon.Data.FlatData;
import net.hynse.balloon.Data.PlayerData;
import net.hynse.balloon.Event.LeashEvent;
import net.hynse.balloon.Event.PlayerEvent;
import net.hynse.balloon.Gui.ControllerGUI;
import net.hynse.balloon.Gui.ItemGUI;
import net.hynse.balloon.Gui.SelectionGUI;
import net.hynse.balloon.Util.BalloonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class Balloon extends FoliaWrappedJavaPlugin implements Listener {
    public static Balloon instance;

    public static PlayerData playerData;
    public static BalloonUtil balloonUtil;
    public static ItemGUI itemGUI;
    public static SelectionGUI selectionGUI;
    public static FlatData flatData;

    public Material balloonItem = Material.FEATHER;
    public NamespacedKey balloonCleanUpKey = new NamespacedKey(this, "balloonCleanUp");
    public File configFile;
    public FileConfiguration config;

    @Override
    public void onEnable() {
        Register();
        //scheduleBalloonCleanupTask();
    }
    @Override
    public void onDisable() {
        flatData.saveConfig();
    }


    private void Register() {
        instance = this;
        playerData = new PlayerData();
        balloonUtil = new BalloonUtil();
        itemGUI = new ItemGUI();
        selectionGUI = new SelectionGUI();
        flatData = new FlatData();
        Objects.requireNonNull(getCommand("balloon")).setExecutor(new balloon());
        getServer().getPluginManager().registerEvents(new LeashEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvent(), this);
        getServer().getPluginManager().registerEvents(new ControllerGUI(), this);

        getServer().getPluginManager().registerEvents(this, this);
        flatData.loadConfig();
    }

//    private void scheduleBalloonCleanupTask() {
//        new WrappedRunnable() {
//            @Override
//            public void run() {
//                Bukkit.getOnlinePlayers().forEach(player -> {
//                    new WrappedRunnable() {
//                        @Override
//                        public void run() {
//                            player.getWorld().getNearbyEntities(player.getLocation(), 16, 16, 16).forEach(entity -> {
//                                if (entity instanceof Parrot parrot) {
//                                    if (parrot.getPersistentDataContainer().has(balloonCleanUpKey, PersistentDataType.BOOLEAN) && !parrot.isLeashed()) {
//                                        parrot.getPassengers().forEach(Entity::remove);
//                                        playerData.removeLinked(player.getUniqueId());
//                                        parrot.remove();
//                                    }
//                                }
//                            });
//                        }
//                    }.runTaskAtLocation(Balloon.instance, player.getLocation());
//                });
//            }
//        }.runTaskTimer(Balloon.instance, 10 * 20, 10 * 20);
//    }

}