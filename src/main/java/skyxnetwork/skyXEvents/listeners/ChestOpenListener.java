package skyxnetwork.skyXEvents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.persistence.PersistentDataType;
import skyxnetwork.skyXEvents.SkyXEvents;
import skyxnetwork.skyXEvents.managers.ConfigManager;
import skyxnetwork.skyXEvents.managers.HologramManager;
import skyxnetwork.skyXEvents.utils.ChestConfig;
import skyxnetwork.skyXEvents.utils.ItemUtils;

import java.util.*;

public class ChestOpenListener implements Listener {

    // Map pour garder trace des joueurs ayant déjà looté chaque coffre
    private final Map<Location, Set<UUID>> claimedChests = new HashMap<>();

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player player = (Player) e.getPlayer();

        if (!(e.getInventory().getHolder() instanceof Chest)) return;
        Chest chest = (Chest) e.getInventory().getHolder();
        Location loc = chest.getLocation();

        // Initialiser le set si absent
        claimedChests.putIfAbsent(loc, new HashSet<>());

        // Vérifie si le joueur a déjà claim ce coffre
        if (claimedChests.get(loc).contains(player.getUniqueId())) {
            player.sendMessage("§cYou have already claimed this chest!");
            e.setCancelled(true); // empêche l'ouverture si voulu
            return;
        }

        // Marque le coffre comme looté globalement
        if (chest.getPersistentDataContainer().has(SkyXEvents.CHEST_LOOTED_KEY)) return;
        chest.getPersistentDataContainer().set(SkyXEvents.CHEST_LOOTED_KEY, PersistentDataType.BYTE, (byte) 1);

        // ✅ Exécuter les commandes
        List<String> commands = chest.getPersistentDataContainer()
                .get(SkyXEvents.CHEST_COMMANDS_KEY, SkyXEvents.STRING_LIST);
        if (commands != null) {
            for (String cmd : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
            }
        }

        // ✅ Donne les items directement au joueur
        ChestConfig chestConfig = ConfigManager.fillChestWithRandomLoot(chest);
        if (chestConfig != null) {
            List<?> rawItems = chestConfig.getConfig().getList("chest.items");
            if (rawItems != null && !rawItems.isEmpty()) {
                if (player.getInventory().firstEmpty() == -1) {
                    // Inventaire plein
                    player.sendMessage("§cYour inventory is full! Please clear space to claim the chest.");
                } else {
                    for (Object o : rawItems) {
                        if (o instanceof Map<?, ?>) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> map = (Map<String, Object>) o;
                            player.getInventory().addItem(ItemUtils.createFromMap(map));
                        }
                    }
                    // ✅ marque que le joueur a claim ce coffre
                    claimedChests.get(loc).add(player.getUniqueId());
                }
            }
        }

        // ✅ Supprime l'hologramme
        HologramManager.removeHologram(loc);

        // ✅ Supprime le coffre après délai si plus personne ne le regarde
        Bukkit.getScheduler().runTaskLater(SkyXEvents.getInstance(), () -> {
            if (chest.getInventory().getViewers().isEmpty()) {
                chest.getBlockInventory().clear();
                chest.getBlock().setType(Material.AIR);
                // ✅ nettoyer la mémoire pour ce coffre
                claimedChests.remove(loc);
            }
        }, 60L); // 3 secondes
    }
}