package net.hynse.balloon.Util;

import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import net.hynse.balloon.Balloon;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
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
        parrot.setInvulnerable(false);
        Objects.requireNonNull(parrot.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(0.4);
        Objects.requireNonNull(parrot.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(1024);
        Objects.requireNonNull(parrot.getAttribute(Attribute.GENERIC_GRAVITY)).setBaseValue(0.1);
        Objects.requireNonNull(parrot.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(-0.5);
        Objects.requireNonNull(parrot.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(1024);
        Objects.requireNonNull(parrot.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(1024);
        parrot.setHealth(1024);

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
        balloon.setHeadPose(new EulerAngle(Math.toRadians(180), Math.toRadians(0), Math.toRadians(0)));

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
        new BalloonFloatTask(parrot, player).runTaskTimerAtEntity(Balloon.instance, parrot, 1, Balloon.instance.delay);

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
        private final Player player;

        public BalloonFloatTask(Parrot parrot, Player player) {
            this.parrot = parrot;
            this.player = player;
        }

        @Override
        public void run() {
            if (parrot.getPersistentDataContainer().has(instance.balloonCleanUpKey)) {
                double playerHeadY = player.getLocation().getY() + player.getEyeHeight();
                double parrotY = parrot.getLocation().getY();
                double parrotX = parrot.getLocation().getX();
                double parrotZ = parrot.getLocation().getZ();
                double playerX = player.getLocation().getX();
                double playerZ = player.getLocation().getZ();

                final double radius = 2.0;
                final double height = 3.2;
                final double halfSphereHeight = 3.2;

                double distanceXZSquared = Math.pow(parrotX - playerX, 2) + Math.pow(parrotZ - playerZ, 2);
                double distanceYAboveCylinder = parrotY - (playerHeadY + height - halfSphereHeight);

                final double minDistanceXZSquared = Math.pow(radius, 2);
                final double minDistanceYSquared = Math.pow(radius, 2);

                boolean withinCylinder = (distanceXZSquared < minDistanceXZSquared) && (parrotY <= playerHeadY + height - halfSphereHeight);

                boolean withinSphereCap = (distanceXZSquared + Math.pow(distanceYAboveCylinder, 2) < minDistanceYSquared);

                if (withinCylinder || withinSphereCap) {
                    double distanceSquared = Math.pow(parrotX - playerX, 2) + Math.pow(parrotY - playerHeadY, 2) + Math.pow(parrotZ - playerZ, 2);

                    if (distanceSquared != 0) {
                        double distance = Math.sqrt(distanceSquared);
                        double factor = 0.05 / distance;
                        double pushX = (parrotX - playerX) * factor;
                        double pushY = (parrotY - playerHeadY) * factor;
                        double pushZ = (parrotZ - playerZ) * factor;

                        if (Double.isFinite(pushX) && Double.isFinite(pushY) && Double.isFinite(pushZ)) {
                            Vector pushback = new Vector(pushX, pushY, pushZ);
                            parrot.setVelocity(pushback);
                        }
                    }
                }


                double highAddition = playerHeadY + 0.4;
                double mediumAddition = playerHeadY + 0.6;
                double lowAddition = playerHeadY + 0.8;

                if (parrotY < highAddition) {
                    Vector velocityToAdd = new Vector(0, 0.18, 0);
                    parrot.setVelocity(velocityToAdd);
                    //instance.getLogger().info("High velocity applied");
                    //player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0,highAddition,0), 0, 0, 0, 0, 0.1);
                } else if (parrotY < mediumAddition) {
                    Vector velocityToAdd = new Vector(0, 0.07, 0);
                    parrot.setVelocity(velocityToAdd);
                    //instance.getLogger().info("Medium velocity applied");
                    //player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0,highAddition,0), 0, 0, 0, 0, 0.1);
                } else if (parrotY < lowAddition) {
                    Vector velocityToAdd = new Vector(0, 0.03, 0);
                    parrot.setVelocity(velocityToAdd);
                    //instance.getLogger().info("Low velocity applied");
                    //player.getWorld().spawnParticle(Particle.GLOW, player.getLocation().add(0,highAddition,0), 0, 0, 0, 0, 0.1);
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
                //spawnParticleOutline(player.getLocation(), radius, height, halfSphereHeight);
            } else {
                cancel();
            }

        }
        private void spawnParticleOutline(Location location, double radius, double height, double halfSphereHeight) {
            // Spawn particles for the cylindrical part
            for (double y = 0; y <= height - halfSphereHeight; y += 0.1) {
                for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 16) {
                    double x = radius * Math.cos(theta);
                    double z = radius * Math.sin(theta);
                    location.getWorld().spawnParticle(Particle.WAX_OFF, location.clone().add(x, y, z), 0, 0, 0, 0, 0.1);
                }
            }

            // Spawn particles for the spherical cap part
            double centerY = height - halfSphereHeight;
            for (double phi = 0; phi <= Math.PI / 2; phi += Math.PI / 16) {
                for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 16) {
                    double x = radius * Math.sin(phi) * Math.cos(theta);
                    double y = radius * Math.cos(phi);
                    double z = radius * Math.sin(phi) * Math.sin(theta);
                    location.getWorld().spawnParticle(Particle.WAX_ON, location.clone().add(x, centerY + y, z), 0, 0, 0, 0, 0.1);
                }
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
