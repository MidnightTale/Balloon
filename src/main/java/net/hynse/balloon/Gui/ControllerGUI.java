package net.hynse.balloon.Gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static net.hynse.balloon.Balloon.*;

public class ControllerGUI implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().title().equals(Component.text("Select Balloon").color(NamedTextColor.BLUE))) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() != Material.AIR) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null && itemMeta.hasCustomModelData()) {
                    int customModelData = itemMeta.getCustomModelData();
                    if (customModelData == 77677) {
                        balloonUtil.disableBalloon(player);
                    } else {
                        balloonUtil.spawnOrUpdateBalloon(player, customModelData);
                    }
                }
                player.updateInventory();
                player.closeInventory();
            }
        }
    }
}
