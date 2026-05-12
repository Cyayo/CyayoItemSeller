package id.seria.itemseller.gui;

import id.seria.itemseller.CyayoSell;
import id.seria.itemseller.config.ConfigManager;
import id.seria.itemseller.config.ItemPrice;
import id.seria.itemseller.config.MenuConfig;
import id.seria.itemseller.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;

import java.util.*;

public class SellGui {

    public static class SellHolder implements InventoryHolder {
        private final UUID uuid;
        private final String menuKey;
        private final Set<Integer> itemSlotSet;

        public SellHolder(UUID uuid, String menuKey, List<Integer> itemSlots) {
            this.uuid = uuid; this.menuKey = menuKey;
            this.itemSlotSet = new HashSet<>(itemSlots);
        }

        public UUID    getPlayerUuid()      { return uuid; }
        public String  getMenuKey()         { return menuKey; }
        public boolean isItemSlot(int slot) { return itemSlotSet.contains(slot); }
        @Override public Inventory getInventory() { return null; }
    }

    private final CyayoSell plugin;
    public SellGui(CyayoSell plugin) { this.plugin = plugin; }

    public void open(Player player, MenuConfig menu) {
        Inventory inv = Bukkit.createInventory(
            new SellHolder(player.getUniqueId(), menu.getKey(), menu.getItemSlots()),
            menu.getRows() * 9, menu.getTitle());
        populate(inv, menu);
        player.openInventory(inv);
        ItemHelper.playSound(player, plugin.getConfigManager().sound("open"));
    }

    public void populate(Inventory inv, MenuConfig menu) {
        ItemStack filler = makeItem(menu.getFillerMaterial(), menu.getFillerName(), null, menu.getFillerCmd());
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);
        for (int s : menu.getItemSlots()) if (s < inv.getSize()) inv.setItem(s, null);
        updateSellButton(inv, menu, 0);
    }

    public void updateSellButton(Inventory inv, MenuConfig menu, double estimated) {
        ConfigManager cfg = plugin.getConfigManager();
        if (menu.getSellButtonSlot() >= inv.getSize()) return;
        List<String> lore = new ArrayList<>(menu.getSellBtnLore());
        if (cfg.isEstimateEnabled()) {
            lore.add(estimated > 0
                ? cfg.getEstimateLoreLine()
                    .replace("{estimated}", ItemHelper.formatPrice(estimated))
                    .replace("{currency}", cfg.getCurrencyName())
                : cfg.getEstimateNoItem());
        }
        inv.setItem(menu.getSellButtonSlot(), makeItem(
            menu.getSellBtnMaterial(), menu.getSellBtnName(), lore, menu.getSellBtnCmd()));
    }

    public double calculateEstimate(Inventory inv, MenuConfig menu) {
        ConfigManager cfg = plugin.getConfigManager();
        double total = 0;
        for (int slot : menu.getItemSlots()) {
            if (slot >= inv.getSize()) continue;
            ItemStack item = inv.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;
            String key = ItemHelper.getKey(item);
            ItemPrice price = key != null ? cfg.getPrice(key) : null;
            if (price != null && price.isAllowedInMenu(menu.getKey())) {
                String quality = ItemHelper.getCustomQuality(item);
                double unitPrice = price.calculateUnitPrice(quality, cfg.getQualityAliases());
                total += unitPrice * item.getAmount();
            }
        }
        return total;
    }

    public void processSell(Player player, Inventory inv, MenuConfig menu) {
        ConfigManager cfg = plugin.getConfigManager();

        // ExcellentEconomy API
        ExcellentCurrency currency = plugin.getIntegrationManager().getCurrency(cfg.getCurrencyId());
        if (currency == null) {
            plugin.getLogger().severe("Currency '" + cfg.getCurrencyId() + "' tidak ditemukan di ExcellentEconomy!");
            player.sendMessage(cfg.msg(menu, "no-permission"));
            return;
        }

        double totalPrice = 0;
        int    totalItems = 0;
        Map<String, double[]> summary = new LinkedHashMap<>();
        List<Integer> toRemove = new ArrayList<>();

        for (int slot : menu.getItemSlots()) {
            if (slot >= inv.getSize()) continue;
            ItemStack item = inv.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;

            String    key   = ItemHelper.getKey(item);
            ItemPrice price = key != null ? cfg.getPrice(key) : null;

            if (price == null || !price.isAllowedInMenu(menu.getKey())) {
                returnItem(player, item);
                inv.setItem(slot, null); // CLEAR IMMEDIATELY TO PREVENT DUPE
                player.sendMessage(cfg.msg(menu, "cannot-sell"));
                continue;
            }

            String quality = ItemHelper.getCustomQuality(item);
            double unitPrice = price.calculateUnitPrice(quality, cfg.getQualityAliases());

            double itemTotal = unitPrice * item.getAmount();
            totalPrice += itemTotal;
            totalItems += item.getAmount();
            String name = ItemHelper.getDisplayName(item);
            if (summary.containsKey(name)) {
                summary.get(name)[0] += item.getAmount();
                summary.get(name)[1] += itemTotal;
            } else {
                summary.put(name, new double[]{item.getAmount(), itemTotal});
            }
            toRemove.add(slot);
        }

        if (totalItems <= 0) {
            player.sendMessage(cfg.msg(menu, "no-item"));
            ItemHelper.playSound(player, cfg.sound("sell-fail"));
            return;
        }

        // Trigger SellPreEvent
        Map<ItemStack, Double> itemsToSellMap = new HashMap<>();
        // Re-calculate mapping for event
        for (int slot : toRemove) {
            ItemStack it = inv.getItem(slot);
            if (it == null) continue;
            String k = ItemHelper.getKey(it);
            ItemPrice p = cfg.getPrice(k);
            if (p != null) {
                double up = p.calculateUnitPrice(ItemHelper.getCustomQuality(it), cfg.getQualityAliases());
                itemsToSellMap.put(it.clone(), up * it.getAmount());
            }
        }
        
        id.seria.itemseller.api.event.SellPreEvent preEvent = new id.seria.itemseller.api.event.SellPreEvent(player, menu, itemsToSellMap, totalPrice);
        Bukkit.getPluginManager().callEvent(preEvent);
        
        if (preEvent.isCancelled()) {
            // Jika dibatalkan, kembalikan item yang seharusnya dijual
            for (int slot : toRemove) {
                ItemStack it = inv.getItem(slot);
                if (it != null) returnItem(player, it);
                inv.setItem(slot, null);
            }
            return;
        }
        
        // Update total price dari event jika ada perubahan
        totalPrice = preEvent.getTotalPrice();

        for (int slot : toRemove) inv.setItem(slot, null);

        // ExcellentEconomy API: deposit
        plugin.getIntegrationManager().deposit(player, currency, totalPrice);

        // Trigger SellSuccessEvent
        Bukkit.getPluginManager().callEvent(new id.seria.itemseller.api.event.SellSuccessEvent(player, menu, itemsToSellMap, totalPrice, totalItems));

        sendSellMessages(player, cfg, menu, summary, totalItems, totalPrice);
        ItemHelper.playSound(player, cfg.sound("sell-success"));
        updateSellButton(inv, menu, 0);
    }

    private void sendSellMessages(Player player, ConfigManager cfg, MenuConfig menu,
                                  Map<String, double[]> summary, int totalItems, double totalPrice) {
        for (String section : cfg.getSellMessageOrder()) {
            switch (section.toLowerCase()) {
                case "header":
                    player.sendMessage(cfg.rawMsg("header")); break;
                case "item-lines": case "item_lines":
                    for (Map.Entry<String, double[]> e : summary.entrySet())
                        player.sendMessage(cfg.rawMsg("item-line",
                            "{amount}",   String.valueOf((int)e.getValue()[0]),
                            "{name}",     e.getKey(),
                            "{price}",    ItemHelper.formatPrice(e.getValue()[1]),
                            "{currency}", cfg.getCurrencyName()));
                    break;
                case "footer":
                    player.sendMessage(cfg.rawMsg("footer")); break;
                case "total":
                    player.sendMessage(cfg.msg(menu, "total",
                        "{total}",    ItemHelper.formatPrice(totalPrice),
                        "{amount}",   String.valueOf(totalItems),
                        "{currency}", cfg.getCurrencyName()));
                    break;
            }
        }
    }

    private void returnItem(Player player, ItemStack item) {
        Map<Integer, ItemStack> lo = player.getInventory().addItem(item);
        for (ItemStack left : lo.values()) player.getWorld().dropItemNaturally(player.getLocation(), left);
    }

    private ItemStack makeItem(String materialStr, String name, List<String> lore, int cmd) {
        Material mat;
        try { mat = Material.valueOf(materialStr.toUpperCase()); }
        catch (Exception e) { mat = Material.STAINED_GLASS_PANE; }
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(name);
        if (lore != null && !lore.isEmpty()) meta.setLore(lore);
        ItemHelper.setCustomModelData(meta, cmd);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean    isSellGui(Inventory inv)  { return inv != null && inv.getHolder() instanceof SellHolder; }
    public static SellHolder getHolder(Inventory inv)  { return (SellHolder) inv.getHolder(); }
}
