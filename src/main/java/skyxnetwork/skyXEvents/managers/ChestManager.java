package skyxnetwork.skyXEvents.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import skyxnetwork.skyXEvents.SkyXEvents;
import skyxnetwork.skyXEvents.utils.ChestConfig;
import skyxnetwork.skyXEvents.utils.ItemUtils;

import java.util.*;

public class ChestManager {

    // Map pour savoir quels joueurs ont ouvert quel coffre
    private static final Map<Location, Set<UUID>> claimedChests = new HashMap<>();

    // Map pour garder les tâches de despawn afin d'éviter les doublons
    private static final Map<Location, BukkitRunnable> despawnTasks = new HashMap<>();

    public static Chest spawnRandomChest() {
        World world = Bukkit.getWorld(SkyXEvents.getInstance().getConfig().getString("world"));
        if (world == null) {
            Bukkit.getLogger().warning("❌ World not found in config.yml !");
            return null;
        }

        int x = random(get("min_x"), get("max_x"));
        int z = random(get("min_z"), get("max_z"));
        int y = world.getHighestBlockYAt(x, z) + 1;

        Location loc = new Location(world, x, y, z);
        loc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) loc.getBlock().getState();

        String prefix = SkyXEvents.getInstance().getConfig().getString("prefix", "");

        // Remplissage coffre + récupération config
        ChestConfig chestConfig = ConfigManager.fillChestWithRandomLoot(chest);

        // Création hologram statique
        if (chestConfig != null && chestConfig.hasHologramEnabled()) {
            HologramManager.createHologram(loc, chestConfig.getHologramLines());
        }

        // Initialise le set pour le cooldown des joueurs
        claimedChests.putIfAbsent(loc, new HashSet<>());

        int autoRemove = SkyXEvents.getInstance().getConfig().getInt("auto_remove_seconds");

        // Countdown et despawn
        BukkitRunnable task = new BukkitRunnable() {
            int timeLeft = autoRemove;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    chest.getBlock().setType(Material.AIR);
                    HologramManager.removeCountdownHologram(loc);
                    HologramManager.removeStaticHologram(loc);
                    claimedChests.remove(loc);
                    despawnTasks.remove(loc);

                    Bukkit.broadcastMessage(ItemUtils.colorize(prefix + "§cThe chest at §f(" +
                            loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +
                            ") §chas expired and disappeared."));
                    cancel();
                    return;
                }

                HologramManager.createOrUpdateCountdownHologram(loc, timeLeft);
                timeLeft--;
            }
        };
        task.runTaskTimer(SkyXEvents.getInstance(), 0L, 20L);
        despawnTasks.put(loc, task);

        if (chestConfig != null && chestConfig.broadcastEnabled()) {
            Bukkit.broadcastMessage(ItemUtils.colorize(prefix + "§c🎁 A mysterious chest spawned at §f" + x + " " + y + " " + z));
        }

        return chest;
    }

    // Vérifie si un joueur a déjà ouvert un coffre
    public static boolean hasClaimed(Player player, Location loc) {
        return claimedChests.containsKey(loc) && claimedChests.get(loc).contains(player.getUniqueId());
    }

    // Marque un joueur comme ayant ouvert un coffre
    public static void markClaimed(Player player, Location loc) {
        claimedChests.putIfAbsent(loc, new HashSet<>());
        claimedChests.get(loc).add(player.getUniqueId());
    }

    private static int get(String path) {
        return SkyXEvents.getInstance().getConfig().getInt(path);
    }

    private static int random(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}