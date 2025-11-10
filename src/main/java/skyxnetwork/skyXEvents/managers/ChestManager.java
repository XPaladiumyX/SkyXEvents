package skyxnetwork.skyXEvents.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import skyxnetwork.skyXEvents.SkyXEvents;

import java.util.Random;

public class ChestManager {

    public static void spawnRandomChest() {

        World world = Bukkit.getWorld(SkyXEvents.getInstance().getConfig().getString("world"));

        if (world == null) {
            Bukkit.getLogger().warning("❌ World not found in config.yml !");
            return;
        }

        int x = random(SkyXEvents.getInstance().getConfig().getInt("min_x"),
                SkyXEvents.getInstance().getConfig().getInt("max_x"));
        int z = random(SkyXEvents.getInstance().getConfig().getInt("min_z"),
                SkyXEvents.getInstance().getConfig().getInt("max_z"));

        int y = world.getHighestBlockYAt(x, z);
        Location loc = new Location(world, x, y, z);

        loc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) loc.getBlock().getState();

        ConfigManager.fillChestWithRandomLoot(chest);

        Bukkit.broadcastMessage("§c🎁 A mysterious chest has spawned at §f" + x + " " + y + " " + z);
    }

    private static int random(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
