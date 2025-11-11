package skyxnetwork.skyXEvents.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import skyxnetwork.skyXEvents.SkyXEvents;

import java.util.Random;

public class ChestManager {

    public static Chest spawnRandomChest() {

        World world = Bukkit.getWorld(SkyXEvents.getInstance().getConfig().getString("world"));

        if (world == null) {
            Bukkit.getLogger().warning("❌ World not found in config.yml !");
        }

        int x = random(get("min_x"), get("max_x"));
        int z = random(get("min_z"), get("max_z"));
        int y = world.getHighestBlockYAt(x, z);

        Location loc = new Location(world, x, y, z);
        loc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) loc.getBlock().getState();

        ConfigManager.fillChestWithRandomLoot(chest);

        int autoRemove = SkyXEvents.getInstance().getConfig().getInt("auto_remove_seconds");

        Bukkit.getScheduler().runTaskLater(SkyXEvents.getInstance(), () -> {
            if (chest.getPersistentDataContainer().has(SkyXEvents.CHEST_LOOTED_KEY))
                return; // Chest already opened -> don't despawn

            chest.getBlockInventory().clear();
            chest.getBlock().setType(Material.AIR);
        }, autoRemove * 20L);

        Bukkit.broadcastMessage("§c🎁 A mysterious chest spawned at §f" + x + " " + y + " " + z);
        return chest;
    }

    private static int get(String path) {
        return SkyXEvents.getInstance().getConfig().getInt(path);
    }

    private static int random(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
