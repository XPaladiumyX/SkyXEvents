package skyxnetwork.skyXEvents.utils;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ChestConfig {

    private final FileConfiguration config;

    public ChestConfig(FileConfiguration config) {
        this.config = config;
    }

    public boolean hasHologramEnabled() {
        return config.getBoolean("chest.hologram.enabled", false);
    }

    public List<String> getHologramLines() {
        return config.getStringList("chest.hologram.lines");
    }

    public boolean broadcastEnabled() {
        return config.getBoolean("chest.broadcast", false);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
