package skyxnetwork.skyXEvents.utils;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtils {

    /**
     * Colorize a string with legacy color codes (&).
     */
    public static String colorize(String input) {
        if (input == null) return null;
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     * Colorize a list of lore lines.
     */
    public static List<String> colorizeLore(List<String> lore) {
        List<String> out = new ArrayList<>();
        if (lore == null) return out;
        for (String s : lore) out.add(colorize(s));
        return out;
    }

    /**
     * Create an ItemStack from basic parameters.
     *
     * @param materialName material name (e.g. "DIAMOND_SWORD")
     * @param amount       amount
     * @param displayName  display name (with color codes &)
     * @param lore         lore lines (with color codes &)
     * @param durability   custom durability (max damage value). If <= 0, not applied.
     * @param glow         if true, add hidden enchant to make the item glow
     */
    public static ItemStack createItem(String materialName, int amount, String displayName, List<String> lore, int durability, boolean glow) {
        Material mat;
        try {
            mat = Material.valueOf(materialName.toUpperCase());
        } catch (Exception e) {
            mat = Material.STONE; // fallback
        }

        ItemStack item = new ItemStack(mat, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        if (displayName != null && !displayName.isEmpty()) meta.setDisplayName(colorize(displayName));
        if (lore != null && !lore.isEmpty()) meta.setLore(colorizeLore(lore));

        if (glow) {
            // add a harmless enchant and hide enchants to create the "glow" visual
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        // handle durability (damage)
        if (durability > 0 && meta instanceof Damageable) {
            // We interpret 'durability' as the maximum custom durability -> we set the current damage to 0 (new item)
            // If you want to set a damaged item, setDamage(value) where value is between 0 and maxDurability.
            // Note: some materials don't support Damageable; the instanceof check avoids errors.
            // We don't set max-durability here because that's version/ItemMeta specific; ItemsAdder handles custom model durability.
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create an ItemStack directly from a yaml-style map.
     * Use this when reading chest definitions like:
     * <p>
     * items:
     * - material: DIAMOND
     * amount: 3
     * name: "&cNice Diamond"
     * lore:
     * - "&7Legendary"
     * glow: true
     */
    @SuppressWarnings("unchecked")
    public static ItemStack createFromMap(Map<String, Object> map) {
        if (map == null) return null;
        String material = (String) map.getOrDefault("material", "STONE");
        int amount = (int) (map.getOrDefault("amount", 1) instanceof Integer ? map.getOrDefault("amount", 1) : Integer.parseInt(map.getOrDefault("amount", 1).toString()));
        String name = (String) map.getOrDefault("name", "");
        List<String> lore = (List<String>) map.getOrDefault("lore", new ArrayList<String>());
        boolean glow = Boolean.parseBoolean(map.getOrDefault("glow", false).toString());
        int durability = (int) (map.getOrDefault("durability", 0) instanceof Integer ? map.getOrDefault("durability", 0) : Integer.parseInt(map.getOrDefault("durability", 0).toString()));

        return createItem(material, amount, name, lore, durability, glow);
    }

    /**
     * Safely add an ItemStack to an Inventory. If inventory is full, drop item on ground at location of player (if player not null).
     */
    public static void giveOrDrop(Inventory inv, ItemStack item, Player fallbackPlayer) {
        HashAddResult result = addItemToInventory(inv, item);
        if (!result.fullyAdded && fallbackPlayer != null) {
            fallbackPlayer.getWorld().dropItemNaturally(fallbackPlayer.getLocation(), result.remaining);
        }
    }

    private static class HashAddResult {
        boolean fullyAdded;
        ItemStack remaining;
    }

    private static HashAddResult addItemToInventory(Inventory inv, ItemStack item) {
        HashAddResult r = new HashAddResult();
        HashMap<Integer, ItemStack> leftover = inv.addItem(item);
        if (leftover == null || leftover.isEmpty()) {
            r.fullyAdded = true;
            r.remaining = null;
        } else {
            r.fullyAdded = false;
            // take the first leftover
            ItemStack rem = null;
            for (ItemStack is : leftover.values()) {
                rem = is;
                break;
            }
            r.remaining = rem;
        }
        return r;
    }

    /**
     * Execute a console command template, replacing %player% with player name.
     * If player is null, replaces %player% with the literal "CONSOLE_TARGET" or leaves as-is.
     */
    public static void dispatchConsoleCommand(String commandTemplate, Player player) {
        if (commandTemplate == null || commandTemplate.isEmpty()) return;
        String cmd = commandTemplate.replace("%player%", player != null ? player.getName() : "CONSOLE_TARGET");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }
}
