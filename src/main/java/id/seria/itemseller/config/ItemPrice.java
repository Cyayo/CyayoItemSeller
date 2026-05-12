package id.seria.itemseller.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPrice {
    public enum ItemType { MMOITEMS, VANILLA }

    private final ItemType   type;
    private final String     mmoType, mmoId, material, key;
    private final double     price;
    private final List<String> allowedMenus;
    private final Map<String, Double> qualityPrices;

    private ItemPrice(ItemType type, String mmoType, String mmoId, String material,
                      double price, List<String> allowedMenus, Map<String, Double> qualityPrices) {
        this.type = type; this.mmoType = mmoType; this.mmoId = mmoId;
        this.material = material; this.price = price; this.allowedMenus = allowedMenus;
        this.qualityPrices = qualityPrices != null ? qualityPrices : new HashMap<>();
        this.key = type == ItemType.MMOITEMS
            ? (mmoType + ":" + mmoId).toUpperCase()
            : ("vanilla:" + material).toUpperCase();
    }

    public static ItemPrice mmoitem(String type, String id, double price, List<String> allowed, Map<String, Double> qualityPrices) {
        return new ItemPrice(ItemType.MMOITEMS, type.toUpperCase(), id.toUpperCase(), null, price, allowed, qualityPrices);
    }
    public static ItemPrice vanilla(String material, double price, List<String> allowed) {
        return new ItemPrice(ItemType.VANILLA, null, null, material.toUpperCase(), price, allowed, null);
    }

    public boolean isAllowedInMenu(String menuKey) {
        if (allowedMenus == null || allowedMenus.isEmpty()) return true;
        for (String m : allowedMenus) if (m.equalsIgnoreCase(menuKey)) return true;
        return false;
    }

    public double calculateUnitPrice(String quality, Map<String, String> qualityAliases) {
        if (quality != null) {
            // Translate long string to short alias if available
            String alias = (qualityAliases != null) ? qualityAliases.getOrDefault(quality, quality) : quality;

            // Local override (using alias or original string)
            if (qualityPrices.containsKey(alias)) {
                return qualityPrices.get(alias);
            }
        }
        return price;
    }

    public String getKey()   { return key; }
    public double getPrice() { return price; }
}
