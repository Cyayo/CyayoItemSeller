package id.seria.itemseller.gui;

import id.seria.itemseller.CyayoSell;
import id.seria.itemseller.config.ConfigManager;
import id.seria.itemseller.config.MenuConfig;
import id.seria.itemseller.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class SellListener implements Listener {
    private final CyayoSell plugin;
    public SellListener(CyayoSell plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Inventory top = e.getView().getTopInventory();
        if (!SellGui.isSellGui(top)) return;
        SellGui.SellHolder h = SellGui.getHolder(top);
        for (int slot : e.getRawSlots())
            if (slot < top.getSize() && !h.isItemSlot(slot)) { e.setCancelled(true); return; }
        MenuConfig menu = plugin.getConfigManager().getMenu(h.getMenuKey());
        if (menu != null) scheduleEstimate((Player) e.getWhoClicked(), top, menu);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        Inventory top = e.getView().getTopInventory();
        if (!SellGui.isSellGui(top)) return;

        SellGui.SellHolder h = SellGui.getHolder(top);
        ConfigManager cfg = plugin.getConfigManager();
        MenuConfig menu = cfg.getMenu(h.getMenuKey());
        if (menu == null) { e.setCancelled(true); return; }

        int raw = e.getRawSlot();
        boolean inTop = raw < top.getSize();

        if (inTop && raw == menu.getSellButtonSlot()) {
            e.setCancelled(true);
            if (!p.hasPermission("itemsell.use")) {
                p.sendMessage(cfg.msg(menu, "no-use-permission"));
                ItemHelper.playSound(p, cfg.sound("error")); return;
            }
            plugin.getSellGui().processSell(p, top, menu);
            return;
        }
        if (inTop && h.isItemSlot(raw)) {
            if (!p.hasPermission("itemsell.use")) {
                e.setCancelled(true);
                p.sendMessage(cfg.msg(menu, "no-use-permission"));
                ItemHelper.playSound(p, cfg.sound("error")); return;
            }
            scheduleEstimate(p, top, menu); return;
        }
        if (inTop) { e.setCancelled(true); return; }

        if (!inTop && e.isShiftClick()) {
            e.setCancelled(true);
            if (!p.hasPermission("itemsell.use")) { p.sendMessage(cfg.msg(menu, "no-use-permission")); return; }
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            for (int slot : menu.getItemSlots()) {
                if (slot >= top.getSize()) continue;
                ItemStack ex = top.getItem(slot);
                if (ex == null || ex.getType() == Material.AIR) {
                    top.setItem(slot, clicked.clone());
                    p.getInventory().setItem(e.getSlot(), null);
                    scheduleEstimate(p, top, menu); return;
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        if (!SellGui.isSellGui(inv)) return;
        MenuConfig menu = plugin.getConfigManager().getMenu(SellGui.getHolder(inv).getMenuKey());
        if (menu == null) return;
        for (int slot : menu.getItemSlots()) {
            if (slot >= inv.getSize()) continue;
            ItemStack item = inv.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;
            inv.setItem(slot, null);
            Map<Integer, ItemStack> lo = p.getInventory().addItem(item);
            for (ItemStack left : lo.values()) p.getWorld().dropItemNaturally(p.getLocation(), left);
        }
    }

    private void scheduleEstimate(Player p, Inventory inv, MenuConfig menu) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline() || !p.getOpenInventory().getTopInventory().equals(inv)) return;
            double est = plugin.getSellGui().calculateEstimate(inv, menu);
            plugin.getSellGui().updateSellButton(inv, menu, est);
        }, 1L);
    }
}
