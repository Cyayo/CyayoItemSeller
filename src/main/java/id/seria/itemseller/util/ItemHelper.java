package id.seria.itemseller.util;

import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemHelper {

    public static String getKey(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        try {
            NBTItem nbt = NBTItem.get(item);
            String t = nbt.getString("MMOITEMS_ITEM_TYPE");
            String i = nbt.getString("MMOITEMS_ITEM_ID");
            if (t != null && !t.isEmpty() && i != null && !i.isEmpty())
                return (t+":"+i).toUpperCase();
        } catch (Exception ignored) {}
        return ("vanilla:"+item.getType().name()).toUpperCase();
    }

    public static String getCustomQuality(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        try {
            NBTItem nbt = NBTItem.get(item);
            String q = nbt.getString("MMOITEMS_CUSTOM_QUALITY");
            if (q != null && !q.isEmpty()) return q;
        } catch (Exception ignored) {}
        return null;
    }

    public static String getDisplayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) return meta.getDisplayName();
        return item.getType().name().toLowerCase().replace("_"," ");
    }

    public static void playSound(Player player, String sound) {
        if (sound == null || sound.isEmpty()) return;
        try { player.playSound(player.getLocation(), sound, 1f, 1f); } catch (Exception ignored) {}
    }

    public static String formatPrice(double price) {
        if (price == Math.floor(price)) return String.valueOf((long) price);
        return String.format("%.1f", price);
    }

    public static void setCustomModelData(ItemMeta meta, int cmd) {
        if (cmd <= 0) return;
        try { meta.getClass().getMethod("setCustomModelData", Integer.class).invoke(meta, cmd); }
        catch (Exception ignored) {}
    }
}
