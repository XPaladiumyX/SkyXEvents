package skyxnetwork.skyXEvents.managers;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import skyxnetwork.skyXEvents.SkyXEvents;
import skyxnetwork.skyXEvents.utils.ChestConfig;
import skyxnetwork.skyXEvents.utils.ItemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConfigManager {

    public static List<YamlConfiguration> chests = new ArrayList<>();

    public static void loadChestFiles() {
        chests.clear();

        File folder = new File(SkyXEvents.getInstance().getDataFolder(), "chests");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.getName().endsWith(".yml")) {
                chests.add(YamlConfiguration.loadConfiguration(f));
            }
        }

        Bukkit.getLogger().info("[SkyXEvents] Loaded " + chests.size() + " chest configurations.");
    }


    public static ChestConfig fillChestWithRandomLoot(Chest chest) {
        if (chests.isEmpty()) return null;

        YamlConfiguration c = chests.get(new Random().nextInt(chests.size()));
        ChestConfig chestConfig = new ChestConfig(c);

        chest.getInventory().clear();

        List<?> rawItems = c.getList("chest.items");
        List<String> rawCommands = c.getStringList("chest.commands");

        // ✅ Ajout d'items dans le coffre
        if (rawItems != null) {
            for (Object o : rawItems) {
                if (o instanceof Map<?, ?> map) {
                    chest.getInventory().addItem(ItemUtils.createFromMap((Map<String, Object>) map));
                }
            }
        }

        // ✅ Exécute commandes quand le coffre sera ouvert
        if (!rawCommands.isEmpty()) {
            chest.getPersistentDataContainer().set(
                    SkyXEvents.CHEST_COMMANDS_KEY,
                    SkyXEvents.STRING_LIST,
                    rawCommands
            );
        }

        chest.update();

        return chestConfig; // retourne pour que ChestManager puisse accéder à l’hologram
    }

    public static void createDefaultChestIfMissing() {
        File folder = new File(SkyXEvents.getInstance().getDataFolder(), "chests");
        if (!folder.exists()) folder.mkdirs();

        if (folder.listFiles() != null && folder.listFiles().length > 0) return;

        File chestFile = new File(folder, "chest1.yml");
        SkyXEvents.getInstance().saveResource("chests/chest1.yml", false);
        chestFile.renameTo(new File(folder, "chest1.yml"));

        Bukkit.getLogger().info("[SkyXEvents] Default chest.yml created.");
    }

}