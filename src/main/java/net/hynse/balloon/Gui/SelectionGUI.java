package net.hynse.balloon.Gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static net.hynse.balloon.Balloon.itemGUI;

public class SelectionGUI {
    public void openBalloonGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, Component.text("Select Balloon").color(NamedTextColor.BLUE));

        gui.addItem(itemGUI.balloonOption("Balloon 1", 41210));
        gui.addItem(itemGUI.balloonOption("Balloon 2", 41211));
        gui.addItem(itemGUI.balloonOption("Balloon 3", 41212));
        gui.addItem(itemGUI.balloonOption("Balloon 4", 41213));

        gui.addItem(itemGUI.disableOption());

        player.openInventory(gui);
    }
}
