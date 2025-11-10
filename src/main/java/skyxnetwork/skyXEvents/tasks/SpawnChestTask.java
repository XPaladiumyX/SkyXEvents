package skyxnetwork.skyXEvents.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import skyxnetwork.skyXEvents.managers.ChestManager;

public class SpawnChestTask extends BukkitRunnable {
    @Override
    public void run() {
        ChestManager.spawnRandomChest();
    }
}
