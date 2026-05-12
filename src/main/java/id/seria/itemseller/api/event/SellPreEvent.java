package id.seria.itemseller.api.event;

import id.seria.itemseller.config.MenuConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Event yang dipicu sesaat sebelum transaksi penjualan diproses.
 * Dapat dibatalkan.
 */
public class SellPreEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private final Player player;
    private final MenuConfig menu;
    private final Map<ItemStack, Double> itemsToSell;
    private double totalPrice;

    public SellPreEvent(@NotNull Player player, @NotNull MenuConfig menu, @NotNull Map<ItemStack, Double> itemsToSell, double totalPrice) {
        this.player = player;
        this.menu = menu;
        this.itemsToSell = itemsToSell;
        this.totalPrice = totalPrice;
    }

    @NotNull
    public Player getPlayer() { return player; }

    @NotNull
    public MenuConfig getMenu() { return menu; }

    @NotNull
    public Map<ItemStack, Double> getItemsToSell() { return itemsToSell; }

    public double getTotalPrice() { return totalPrice; }

    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @NotNull
    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    @NotNull
    public static HandlerList getHandlerList() { return HANDLERS; }
}
