package skyxnetwork.skyXEvents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.persistence.PersistentDataType;
import skyxnetwork.skyXEvents.SkyXEvents;
import skyxnetwork.skyXEvents.managers.ChestManager;
import skyxnetwork.skyXEvents.managers.ConfigManager;
import skyxnetwork.skyXEvents.managers.HologramManager;
import skyxnetwork.skyXEvents.utils.ChestConfig;
import skyxnetwork.skyXEvents.utils.ItemUtils;

import java.util.List;
import java.util.Map;

public class ChestOpenListener implements Listener {

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player player = (Player) e.getPlayer();

        if (!(e.getInventory().getHolder() instanceof Chest)) return;
        Chest chest = (Chest) e.getInventory().getHolder();
        Location loc = chest.getLocation();

        // ❌ Si le coffre n'a pas le TAG, on laisse l'ouverture vanilla
        if (!chest.getPersistentDataContainer().has(SkyXEvents.CHEST_LOOTED_KEY, PersistentDataType.INTEGER)) {
            return;
        }

        String prefix = SkyXEvents.getInstance().getConfig().getString("prefix", "");

        // Vérifie si le joueur a déjà ouvert le coffre
        if (ChestManager.hasClaimed(player, loc)) {
            player.sendMessage(ItemUtils.colorize(prefix + "§cYou have already claimed this chest!"));
            e.setCancelled(true);
            return;
        }

        // Annule l'ouverture réelle du coffre (pas de doublon de cooldown)
        e.setCancelled(true);

        // Donne les items au joueur
        ChestConfig chestConfig = ConfigManager.fillChestWithRandomLoot(chest);
        if (chestConfig != null) {
            List<?> rawItems = chestConfig.getConfig().getList("chest.items");
            if (rawItems != null && !rawItems.isEmpty()) {
                for (Object o : rawItems) {
                    if (o instanceof Map<?, ?> map) {
                        player.getInventory().addItem(ItemUtils.createFromMap((Map<String, Object>) map));
                    }
                }
            }
        }

        // Exécute les commandes attachées au coffre
        List<String> commands = chest.getPersistentDataContainer().get(SkyXEvents.CHEST_COMMANDS_KEY, SkyXEvents.STRING_LIST);
        if (commands != null) {
            for (String cmd : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
            }
        }

        // Marque le joueur comme ayant ouvert le coffre
        ChestManager.markClaimed(player, loc);

        // Message global pour dire qu'un joueur a ouvert le coffre
        Bukkit.broadcastMessage(ItemUtils.colorize(prefix + "§a" + player.getName() +
                " §ehas opened the chest at §f(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")"));

        // Supprime uniquement les hologrammes statiques pour cleaner
        HologramManager.removeStaticHologram(loc);
    }
}