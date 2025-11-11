package skyxnetwork.skyXEvents.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import skyxnetwork.skyXEvents.SkyXEvents;

import java.util.List;

public class HologramManager {

    // Supprimer les hologrammes à une location
    public static void removeHologram(Location loc) {
        loc.getWorld().getNearbyEntities(loc, 1, 5, 1).stream()
                .filter(e -> e instanceof ArmorStand && e.isCustomNameVisible())
                .forEach(Entity::remove);
    }

    // Crée un hologramme statique (ancienne fonction)
    public static void createHologram(Location loc, List<String> lines) {
        if (lines == null || lines.isEmpty()) return;

        double yOffset = 1.7;
        for (int i = 0; i < lines.size(); i++) {
            String line = ChatColor.translateAlternateColorCodes('&', lines.get(i));
            ArmorStand as = loc.getWorld().spawn(loc.clone().add(0.5, yOffset + (lines.size() - i - 1) * 0.25, 0.5), ArmorStand.class);
            as.setInvisible(true);
            as.setMarker(true);
            as.setGravity(false);
            as.setCustomNameVisible(true);
            as.setCustomName(line);
        }
    }

    /**
     * Crée un hologramme avec countdown
     *
     * @param loc     Location du coffre
     * @param seconds temps avant despawn
     */
    public static void createCountdownHologram(Location loc, int seconds) {
        double yOffset = 2.3; // un peu au-dessus du coffre
        ArmorStand hologram = loc.getWorld().spawn(loc.clone().add(0.5, yOffset, 0.5), ArmorStand.class);
        hologram.setInvisible(true);
        hologram.setMarker(true);
        hologram.setGravity(false);
        hologram.setCustomNameVisible(true);

        // Tâche répétée pour update le countdown toutes les secondes
        new BukkitRunnable() {
            int timeLeft = seconds;

            @Override
            public void run() {
                if (hologram.isDead() || timeLeft <= 0) {
                    hologram.remove();
                    cancel();
                    return;
                }

                // Texte du hologram
                hologram.setCustomName(ChatColor.RED + "Dispawn Automatically in " + timeLeft + "s");

                // Optionnel : descente progressive
                Location current = hologram.getLocation();
                hologram.teleport(current.subtract(0, 0.02, 0)); // descend doucement

                timeLeft--;
            }
        }.runTaskTimer(SkyXEvents.getInstance(), 0L, 20L); // toutes les secondes
    }
}