package id.seria.itemseller.api.event;

import id.seria.itemseller.config.MenuConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Event yang dipicu setelah transaksi penjualan berhasil dilakukan.
 */
public class SellSuccessEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final MenuConfig menu;
    private final Map<ItemStack, Double> soldItems;
    private final double totalPrice;
    private final int totalAmount;

    public SellSuccessEvent(@NotNull Player player, @NotNull MenuConfig menu, @NotNull Map<ItemStack, Double> soldItems, double totalPrice, int totalAmount) {
        this.player = player;
        this.menu = menu;
        this.soldItems = soldItems;
        this.totalPrice = totalPrice;
        this.totalAmount = totalAmount;
    }

    @NotNull
    public Player getPlayer() { return player; }

    @NotNull
    public MenuConfig getMenu() { return menu; }

    @NotNull
    public Map<ItemStack, Double> getSoldItems() { return soldItems; }

    public double getTotalPrice() { return totalPrice; }

    public int getTotalAmount() { return totalAmount; }

    @NotNull
    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    @NotNull
    public static HandlerList getHandlerList() { return HANDLERS; }
}
