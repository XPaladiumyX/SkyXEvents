package skyxnetwork.skyXEvents.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.List;

public class HologramManager {

    // Supprime uniquement les hologrammes statiques
    public static void removeStaticHologram(Location loc) {
        loc.getWorld().getNearbyEntities(loc, 1, 5, 1).stream()
                .filter(e -> e instanceof ArmorStand as && as.isCustomNameVisible()
                        && !as.getCustomName().contains("Dispawn"))
                .forEach(Entity::remove);
    }

    // Supprime un hologramme de countdown
    public static void removeCountdownHologram(Location loc) {
        loc.getWorld().getNearbyEntities(loc, 1, 5, 1).stream()
                .filter(e -> e instanceof ArmorStand as && as.isCustomNameVisible()
                        && as.getCustomName().contains("Dispawn"))
                .forEach(Entity::remove);
    }

    // Crée un hologramme statique
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

    // Crée un hologramme avec countdown basé sur auto_remove_seconds
    public static void createOrUpdateCountdownHologram(Location loc, int seconds) {
        // Supprime l'ancien hologramme s'il existe
        removeCountdownHologram(loc);

        double yOffset = 2.3;
        ArmorStand hologram = loc.getWorld().spawn(loc.clone().add(0.5, yOffset, 0.5), ArmorStand.class);
        hologram.setInvisible(true);
        hologram.setMarker(true);
        hologram.setGravity(false);
        hologram.setCustomNameVisible(true);
        hologram.setCustomName(ChatColor.RED + "Dispawn Automatically in " + seconds + "s");
    }
}
