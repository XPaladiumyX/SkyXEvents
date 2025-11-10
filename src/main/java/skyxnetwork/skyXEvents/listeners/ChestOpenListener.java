package skyxnetwork.skyXEvents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import skyxnetwork.skyXEvents.SkyXEvents;

import java.util.List;

public class ChestOpenListener implements Listener {

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {

        if (!(e.getPlayer() instanceof Player player)) return;
        if (!(e.getInventory().getHolder() instanceof Chest chest)) return;

        List<String> commands = chest.getPersistentDataContainer()
                .get(SkyXEvents.CHEST_COMMANDS_KEY, SkyXEvents.STRING_LIST);

        if (commands == null || commands.isEmpty()) return;

        for (String cmd : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
        }
    }
}