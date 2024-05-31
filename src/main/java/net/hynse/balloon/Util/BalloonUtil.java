package net.hynse.balloon.Util;

import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import net.hynse.balloon.Balloon;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;

import static net.hynse.balloon.Balloon.*;

public class BalloonUtil {

    public void spawnOrUpdateBalloon(Player player, int customModelData) {
        UUID playerId = player.getUniqueId();
        UUID linkId = playerData.getLinked(playerId);
        playerData.putBalloonShow(playerId, true);
        playerData.putCustomModelData(playerId,customModelData);
        if (linkId != null) {
            //instance.getLogger().info(player + "updateBalloon" + customModelData);
            updateBalloon(linkId, customModelData, player);
            playerData.putBalloonShow(playerId, true);
        } else {
            spawnBalloon(player ,playerId ,customModelData);
        }
    }

    public void spawnBalloon(Player player, UUID playerId, int customModelData) {
        // Spawn the parrot
        Parrot parrot = (Parrot) player.getWorld().spawnEntity(player.getLocation().add(1, 2, 1), EntityType.PARROT);
        // Spawn the armor stand
        ArmorStand balloon = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.ARMOR_STAND);

        UUID parrotId = parrot.getUniqueId();
        playerData.putLinked(playerId, parrotId);

        // Configure the parrot
        parrot.setTamed(true);
        parrot.setOwner(player);
        parrot.setLeashHolder(player);
        parrot.setSilent(true);
        parrot.setVariant(Parrot.Variant.GRAY);
        parrot.setAdult();
        parrot.setCollidable(false);
        parrot.setInvisible(true);
        parrot.setBreed(false);
        parrot.setLootTable(null);
        Objects.requireNonNull(parrot.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(0.4);
        Objects.requireNonNull(parrot.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(1024);
        Objects.requireNonNull(parrot.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(1024);

        parrot.getPersistentDataContainer().set(Balloon.instance.balloonCleanUpKey, PersistentDataType.BOOLEAN, true);

        // Configure the armor stand
        balloon.setCollidable(false);
        balloon.setInvisible(true);
        balloon.setInvulnerable(true);
        balloon.setBasePlate(false);
        balloon.setSmall(true);
        balloon.setGravity(false);
        balloon.setArms(false);
        balloon.setMarker(true);
        balloon.setDisabledSlots(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);

        // Set the custom model data for the armor stand's helmet
        ItemStack itemStack = new ItemStack(Balloon.instance.balloonItem);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);
        balloon.getEquipment().setHelmet(itemStack);

        // Ensure that the custom name is hidden
        balloon.setCustomNameVisible(false);

        // Add the armor stand as a passenger to the parrot
        parrot.addPassenger(balloon);
        new BalloonFloatTask(parrot).runTaskTimerAtEntity(Balloon.instance, parrot, 1L, 4L);

        //Balloon.instance.getLogger().info(player + "spawnBalloon" + customModelData);
    }


    private void updateBalloon(UUID linkId, int customModelData, Player player) {
        Entity linkedEntity = null;
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity.getUniqueId().equals(linkId)) {
                linkedEntity = entity;
                break;
            }
        }
        if (linkedEntity instanceof Parrot parrot) {
            parrot.teleportAsync(player.getLocation().add(0,2,0));
            parrot.setLeashHolder(player);
            if (!parrot.getPassengers().isEmpty()) {
                Entity passenger = parrot.getPassengers().getFirst();
                if (passenger instanceof ArmorStand balloon) {
                    ItemStack helmet = balloon.getEquipment().getHelmet();
                    if (helmet != null) {
                        ItemMeta itemMeta = helmet.getItemMeta();
                        if (itemMeta != null) {
                            itemMeta.setCustomModelData(customModelData);
                            helmet.setItemMeta(itemMeta);
                            balloon.getEquipment().setHelmet(helmet);
                        }
                    }
                }
            }
        }
    }

    public static class BalloonFloatTask extends WrappedRunnable {
        private final Parrot parrot;

        public BalloonFloatTask(Parrot parrot) {
            this.parrot = parrot;
        }

        @Override
        public void run() {
            if (parrot.isLeashed() && parrot.getLeashHolder() instanceof Player player) {
                double playerHeadY = player.getLocation().getY() + player.getEyeHeight();
                double parrotY = parrot.getLocation().getY();
                double parrotX = parrot.getLocation().getX();
                double parrotZ = parrot.getLocation().getZ();
                double playerX = player.getLocation().getX();
                double playerZ = player.getLocation().getZ();

                double distanceSquared = Math.pow(parrotX - playerX, 2) + Math.pow(parrotY - playerHeadY, 2) + Math.pow(parrotZ - playerZ, 2);

                final double minDistanceSquared = Math.pow(0.8, 2);
                if (distanceSquared < minDistanceSquared) {
                    if (distanceSquared != 0) {
                        double distance = Math.sqrt(distanceSquared);
                        double factor = 0.32 / distance;
                        double pushX = (parrotX - playerX) * factor;
                        double pushY = (parrotY - playerHeadY) * factor;
                        double pushZ = (parrotZ - playerZ) * factor;

                        if (Double.isFinite(pushX) && Double.isFinite(pushY) && Double.isFinite(pushZ)) {
                            Vector pushback = new Vector(pushX, pushY, pushZ);
                            parrot.setVelocity(pushback);
                        }
                    }
                }

                if (parrotY < playerHeadY + 0.2) {
                    parrot.setVelocity(parrot.getVelocity().setY(0.24));
                }
//                Location playerLocation = player.getLocation();
//                Location parrotLocation = parrot.getLocation();
//
//                distanceSquared = parrotLocation.distanceSquared(playerLocation);
//
//                final double maxDistanceSquared = 32;
//                if (distanceSquared > maxDistanceSquared) {
//                    Location teleportLocation = playerLocation.clone().add(0, 3, 0);
//                    parrot.setLeashHolder(player);
//                    parrot.teleportAsync(teleportLocation);
//                }
            } else {
                cancel();
            }

        }
    }

    public void disableBalloon(Player player) {
        UUID linkId = playerData.getLinked(player.getUniqueId());
        UUID playerId = player.getUniqueId();
        Entity linkedEntity = null;
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity.getUniqueId().equals(linkId)) {
                linkedEntity = entity;
                break;
            }
        }

        if (linkedEntity instanceof Parrot parrot) {
            for (Entity passenger : parrot.getPassengers()) {
                if (passenger instanceof ArmorStand) {
                    passenger.remove();
                }
            }
            playerData.removeBalloonShow(playerId);
            playerData.removeLinked(playerId);
            playerData.removeBalloonCustomModelData(playerId);
            parrot.remove();
        }
    }
}
