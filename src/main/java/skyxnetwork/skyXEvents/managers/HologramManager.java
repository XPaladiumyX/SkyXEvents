package skyxnetwork.skyXEvents.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.List;

public class HologramManager {

    public static void createHologram(Location loc, List<String> lines) {
        var armorStand = loc.getWorld().spawn(loc.clone().add(0.5, 1.7, 0.5), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', String.join("\n", lines)));
    }

    public static void removeHologram(Location loc) {
        loc.getWorld().getNearbyEntities(loc, 1, 2, 1).stream()
                .filter(e -> e instanceof ArmorStand && e.isCustomNameVisible())
                .forEach(Entity::remove);
    }
}
