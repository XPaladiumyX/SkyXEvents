package skyxnetwork.skyXEvents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import skyxnetwork.skyXEvents.SkyXEvents;
import skyxnetwork.skyXEvents.managers.HologramManager;

import java.util.List;

public class ChestOpenListener implements Listener {

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {

        if (!(e.getPlayer() instanceof Player player)) return;
        if (!(e.getInventory().getHolder() instanceof Chest chest)) return;

        if (chest.getPersistentDataContainer().has(SkyXEvents.CHEST_LOOTED_KEY))
            return; // Already used

        chest.getPersistentDataContainer().set(SkyXEvents.CHEST_LOOTED_KEY, PersistentDataType.BYTE, (byte) 1);

        List<String> commands = chest.getPersistentDataContainer()
                .get(SkyXEvents.CHEST_COMMANDS_KEY, SkyXEvents.STRING_LIST);

        if (commands != null)
            commands.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));

        for (ItemStack item : chest.getBlockInventory().getContents()) {
            if (item != null)
                player.getInventory().addItem(item);
        }

        // remove hologram if exists
        HologramManager.removeHologram(chest.getLocation());

        // ✅ délai avant de supprimer le coffre (fix ItemsAdder "TileState null")
        Bukkit.getScheduler().runTaskLater(SkyXEvents.getInstance(), () -> {
            chest.getInventory().clear();
            chest.getBlock().setType(Material.AIR);
        }, 5L);
    }
}