package id.seria.itemseller.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class CyayoSellProvider {

    private static CyayoSellAPI instance = null;

    private CyayoSellProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Mendapatkan instance API CyayoSell.
     * 
     * @return CyayoSellAPI instance
     * @throws IllegalStateException jika API belum didaftarkan (plugin belum aktif)
     */
    @NotNull
    public static CyayoSellAPI get() {
        if (instance == null) {
            throw new IllegalStateException("CyayoSell API is not registered yet!");
        }
        return instance;
    }

    @ApiStatus.Internal
    public static void register(@NotNull CyayoSellAPI api) {
        instance = api;
    }

    @ApiStatus.Internal
    public static void unregister() {
        instance = null;
    }
}
