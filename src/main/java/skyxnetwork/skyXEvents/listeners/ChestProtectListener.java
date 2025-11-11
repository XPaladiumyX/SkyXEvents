package skyxnetwork.skyXEvents.listeners;


import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataType;
import skyxnetwork.skyXEvents.SkyXEvents;

public class ChestProtectListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!(e.getBlock().getState() instanceof Chest chest)) return;

        // ✅ Seulement si c'est un chest event
        if (chest.getPersistentDataContainer().has(SkyXEvents.CHEST_LOOTED_KEY, PersistentDataType.INTEGER)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cYou cannot break an event chest!");
        }
    }
}
