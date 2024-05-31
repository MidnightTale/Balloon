package net.hynse.balloon.Util;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

public class ItemUtil {
    public void setballoonHolderMetadata(ArmorStand entity, int customModelData, Material balloonItem) {
        // Create an ItemStack using the balloonItem
        ItemStack itemStack = new ItemStack(balloonItem);

        // Set custom model data for the item
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);

        entity.getEquipment().setHelmet(itemStack);
        entity.setCollidable(false);
        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.setBasePlate(false);
        entity.setSmall(true);
        entity.setGravity(false);
        entity.setArms(false);
        entity.setMarker(true);
        entity.setHeadPose(new EulerAngle(Math.toRadians(180), Math.toRadians(0), Math.toRadians(0)));
        entity.getEquipment().setHelmet(itemStack);
        entity.setCustomNameVisible(false);
    }

}
