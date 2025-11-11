package skyxnetwork.skyXEvents;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import skyxnetwork.skyXEvents.listeners.ChestOpenListener;
import skyxnetwork.skyXEvents.managers.ChestManager;
import skyxnetwork.skyXEvents.managers.ConfigManager;
import skyxnetwork.skyXEvents.tasks.SpawnChestTask;

import java.util.List;

public class SkyXEvents extends JavaPlugin {

    private static SkyXEvents instance;

    public static SkyXEvents getInstance() {
        return instance;
    }

    public static NamespacedKey CHEST_COMMANDS_KEY;
    public static PersistentDataType<List<String>, List<String>> STRING_LIST;
    public static NamespacedKey CHEST_HOLOGRAM_KEY;
    public static NamespacedKey CHEST_LOOTED_KEY;


    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        ConfigManager.createDefaultChestIfMissing();
        ConfigManager.loadChestFiles();

        int delay = getConfig().getInt("spawn_interval_seconds");

        CHEST_COMMANDS_KEY = new NamespacedKey(this, "chest_commands");
        CHEST_HOLOGRAM_KEY = new NamespacedKey(this, "chest_hologram");
        CHEST_LOOTED_KEY = new NamespacedKey(this, "chest_looted");
        STRING_LIST = PersistentDataType.LIST.strings();

        getServer().getPluginManager().registerEvents(new ChestOpenListener(), this);


        new SpawnChestTask().runTaskTimer(this, 20L, delay * 20L);

        getCommand("skyxevents").setExecutor((sender, command, label, args) -> {
            if (args.length == 0) {
                sender.sendMessage("§c/skyxevents reload | force");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "reload":
                    reloadConfig();
                    ConfigManager.loadChestFiles();
                    sender.sendMessage("§aSkyXEvents reloaded!");
                    return true;

                case "force":
                    ChestManager.spawnRandomChest();
                    sender.sendMessage("§eForced spawn.");
                    return true;
            }
            return false;
        });

        getLogger().info("SkyXEvents enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkyXEvents disabled.");
    }
}
