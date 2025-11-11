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


    /**
     * ✅ Select chest based on chance weight from chestX.yml
     */
    private static YamlConfiguration getRandomChestByChance() {

        double totalChance = 0;
        for (YamlConfiguration c : chests) {
            totalChance += c.getDouble("chest.chance", 0);
        }

        double random = Math.random() * totalChance;
        double current = 0;

        for (YamlConfiguration c : chests) {
            current += c.getDouble("chest.chance", 0);
            if (random <= current) return c;
        }

        return chests.get(0); // fallback

    }

    /**
     * ✅ Fill chest with weighted random loot
     */
    public static ChestConfig fillChestWithRandomLoot(Chest chest) {
        if (chests.isEmpty()) return null;

        YamlConfiguration c = getRandomChestByChance();
        ChestConfig chestConfig = new ChestConfig(c);

        chest.getInventory().clear();

        List<?> rawItems = c.getList("chest.items");
        List<String> rawCommands = c.getStringList("chest.commands");

        // ✅ Ajoute les items dans le coffre
        if (rawItems != null) {
            for (Object o : rawItems) {
                if (o instanceof Map<?, ?> map) {
                    chest.getInventory().addItem(ItemUtils.createFromMap((Map<String, Object>) map));
                }
            }
        }

        // ✅ Stocke les commandes dans le coffre (exécutées à l'ouverture)
        if (!rawCommands.isEmpty()) {
            chest.getPersistentDataContainer().set(
                    SkyXEvents.CHEST_COMMANDS_KEY,
                    SkyXEvents.STRING_LIST,
                    rawCommands
            );
        }

        chest.update();
        return chestConfig;
    }

    public static void createDefaultChestIfMissing() {
        File folder = new File(SkyXEvents.getInstance().getDataFolder(), "chests");
        if (!folder.exists()) folder.mkdirs();

        if (folder.listFiles() != null && folder.listFiles().length > 0) return;

        File chestFile = new File(folder, "chest1.yml");
        SkyXEvents.getInstance().saveResource("chests/chest1.yml", false);
        chestFile.renameTo(new File(folder, "chest1.yml"));

        Bukkit.getLogger().info("[SkyXEvents] Default chest1.yml created.");
    }

}