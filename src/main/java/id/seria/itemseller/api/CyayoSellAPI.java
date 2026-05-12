package id.seria.itemseller.api;

import id.seria.itemseller.config.MenuConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CyayoSellAPI {

    /**
     * Menghitung harga jual sebuah item berdasarkan konfigurasi.
     * 
     * @param item Item yang akan dicek harganya
     * @return Harga item, atau 0 jika item tidak terdaftar atau tidak bisa dijual
     */
    double getItemPrice(@NotNull ItemStack item);

    /**
     * Memeriksa apakah sebuah item dapat dijual di menu tertentu.
     * 
     * @param item Item yang akan dicek
     * @param menuKey Key menu yang dituju
     * @return True jika diperbolehkan, false jika tidak
     */
    boolean canSellInMenu(@NotNull ItemStack item, @NotNull String menuKey);

    /**
     * Membuka menu penjualan untuk player tertentu secara paksa.
     * 
     * @param player Player yang akan dibuka menunya
     * @param menuKey Key menu yang akan dibuka
     * @return True jika berhasil dibuka, false jika menu tidak ditemukan
     */
    boolean openSellMenu(@NotNull Player player, @NotNull String menuKey);

    /**
     * Mendapatkan konfigurasi menu berdasarkan key.
     * 
     * @param menuKey Key menu
     * @return MenuConfig atau null jika tidak ditemukan
     */
    @Nullable
    MenuConfig getMenuConfig(@NotNull String menuKey);
}
