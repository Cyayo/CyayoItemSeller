package id.seria.itemseller.manager;

import id.seria.itemseller.CyayoSell;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;
import su.nightexpress.excellenteconomy.api.currency.operation.NotificationTarget;
import su.nightexpress.excellenteconomy.api.currency.operation.OperationContext;

public class IntegrationManager {

    private final CyayoSell plugin;
    private boolean excellentEconomyEnabled;
    private ExcellentEconomyAPI excellentEconomyApi;
    private OperationContext operationContext;

    public IntegrationManager(CyayoSell plugin) {
        this.plugin = plugin;
        this.excellentEconomyEnabled = Bukkit.getPluginManager().isPluginEnabled("ExcellentEconomy");
        if (this.excellentEconomyEnabled) {
            RegisteredServiceProvider<ExcellentEconomyAPI> provider = Bukkit.getServer().getServicesManager().getRegistration(ExcellentEconomyAPI.class);
            if (provider != null) {
                this.excellentEconomyApi = provider.getProvider();
                this.operationContext = OperationContext.custom("CyayoSell")
                    .silentFor(NotificationTarget.USER, NotificationTarget.EXECUTOR, NotificationTarget.CONSOLE_LOGGER);
            }
        }
    }

    public boolean isExcellentEconomyEnabled() {
        return excellentEconomyEnabled && excellentEconomyApi != null;
    }

    public ExcellentEconomyAPI getApi() {
        return excellentEconomyApi;
    }

    public OperationContext getOperationContext() {
        return operationContext;
    }

    public ExcellentCurrency getCurrency(String currencyId) {
        if (!isExcellentEconomyEnabled()) return null;
        return excellentEconomyApi.getCurrency(currencyId);
    }

    public boolean deposit(org.bukkit.entity.Player player, ExcellentCurrency currency, double amount) {
        if (!isExcellentEconomyEnabled()) return false;
        return excellentEconomyApi.deposit(player, currency, amount, operationContext);
    }
}
