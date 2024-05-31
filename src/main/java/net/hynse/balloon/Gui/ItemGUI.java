package net.hynse.balloon.Gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemGUI {

    public ItemStack balloonOption(String displayName, int customModelData) {
        ItemStack balloon = new ItemStack(Material.FEATHER);
        ItemMeta meta = balloon.getItemMeta();
        meta.displayName(Component.text(displayName));
        meta.setCustomModelData(customModelData);
        balloon.setItemMeta(meta);
        return balloon;
    }

    public ItemStack disableOption() {
        ItemStack disableOption = new ItemStack(Material.BARRIER);
        ItemMeta meta = disableOption.getItemMeta();
        meta.displayName(Component.text("Disable Balloon").color(NamedTextColor.RED));
        meta.setCustomModelData(77677);
        disableOption.setItemMeta(meta);
        return disableOption;
    }
}
