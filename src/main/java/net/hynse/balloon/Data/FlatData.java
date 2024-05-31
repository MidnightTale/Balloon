package net.hynse.balloon.Data;

import net.hynse.balloon.Balloon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.hynse.balloon.Balloon.playerData;

public class FlatData {

    public void loadConfig() {
        Balloon.instance.configFile = new File(Balloon.instance.getDataFolder(), "config.yml");
        if (!Balloon.instance.configFile.exists()) {
            Balloon.instance.configFile.getParentFile().mkdirs();
            Balloon.instance.saveResource("config.yml", false);
        }

        Balloon.instance.config = YamlConfiguration.loadConfiguration(Balloon.instance.configFile);

        ConfigurationSection playersSection = Balloon.instance.config.getConfigurationSection("players");
        if (playersSection != null) {
            for (String uuidString : playersSection.getKeys(false)) {
                UUID playerId = UUID.fromString(uuidString);
                ConfigurationSection playerSection = playersSection.getConfigurationSection(uuidString);
                if (playerSection != null) {
                    UUID linkedId = UUID.fromString(Objects.requireNonNull(playerSection.getString("linkId")));
                    int customModelData = playerSection.getInt("customModelData");
                    boolean show = playerSection.getBoolean("show");

                    playerData.putLinked(playerId, linkedId);
                    playerData.putCustomModelData(playerId, customModelData);
                    playerData.putBalloonShow(playerId, show);
                    Balloon.instance.getLogger().info(
                            "Loaded player data \n" +
                            "UUID: " + playerId +
                            "\nlinkedId: " + linkedId +
                            "\ncustomModelData: " + customModelData +
                            "\nshow: " + show +
                            "\n--------------------");
                }
            }
        }
    }


    public void saveConfig() {
        Balloon.instance.configFile = new File(Balloon.instance.getDataFolder(), "config.yml");
        Balloon.instance.config = new YamlConfiguration();

        ConfigurationSection playersSection = Balloon.instance.config.createSection("players");
        for (Map.Entry<UUID, UUID> entry : playerData.getLinkedMap().entrySet()) {
            for (Map.Entry<UUID, Integer> entry2 : playerData.getBalloonCustomModelDataMap().entrySet()) {
                for (Map.Entry<UUID, Boolean> entry3 : playerData.getBalloonShowMap().entrySet()) {
                    UUID playerId = entry.getKey();
                    String linkedId = entry.getValue().toString();
                    int customModelData = entry2.getValue();
                    boolean show = entry3.getValue();
                    ConfigurationSection playerSection = playersSection.createSection(playerId.toString());
                    playerSection.set("linkId", linkedId);
                    playerSection.set("customModelData", customModelData);
                    playerSection.set("show", show);
                    Balloon.instance.getLogger().info(
                            "Saved player data\n" +
                            "UUID: " + playerId +
                            "\nlinkedId: " + linkedId +
                            "\ncustomModelData: " + customModelData +
                            "\nshow: " + show +
                            "\n--------------------");
                }
            }
        }

        try {
            Balloon.instance.config.save(Balloon.instance.configFile);
        } catch (IOException e) {
            Balloon.instance.getLogger().warning("Failed to save config.yml!");
        }
    }
}
