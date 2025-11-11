package skyxnetwork.skyXEvents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import skyxnetwork.skyXEvents.SkyXEvents;
import skyxnetwork.skyXEvents.managers.ConfigManager;
import skyxnetwork.skyXEvents.managers.HologramManager;
import skyxnetwork.skyXEvents.utils.ChestConfig;
import skyxnetwork.skyXEvents.utils.ItemUtils;

import java.util.*;

public class ChestOpenListener implements Listener {

    // Map pour savoir quels joueurs ont déjà ouvert quel coffre
    private final Map<Location, Set<UUID>> claimedChests = new HashMap<>();

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player player = (Player) e.getPlayer();

        if (!(e.getInventory().getHolder() instanceof Chest)) return;
        Chest chest = (Chest) e.getInventory().getHolder();
        Location loc = chest.getLocation();

        String prefix = SkyXEvents.getInstance().getConfig().getString("prefix", "");

        // Initialise le set si nécessaire
        claimedChests.putIfAbsent(loc, new HashSet<>());

        // Si le joueur a déjà ouvert ce coffre, on bloque
        if (claimedChests.get(loc).contains(player.getUniqueId())) {
            player.sendMessage(ItemUtils.colorize(prefix + "§cYou have already claimed this chest!"));
            e.setCancelled(true);
            return;
        }

        // Annule l'ouverture réelle du coffre (pas de cooldown relancé)
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

        // Ajoute le joueur au set pour cooldown
        claimedChests.get(loc).add(player.getUniqueId());

        // Message global pour dire qu'un joueur a ouvert le coffre
        Bukkit.broadcastMessage(ItemUtils.colorize(prefix + "§a" + player.getName() +
                " §ehas opened the chest at §f(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")"));

        // Supprime uniquement les hologrammes statiques pour cleaner
        HologramManager.removeStaticHologram(loc);
    }
}