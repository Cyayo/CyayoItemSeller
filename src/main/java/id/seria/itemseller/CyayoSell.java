package id.seria.itemseller;

import id.seria.itemseller.api.CyayoSellAPI;
import id.seria.itemseller.api.CyayoSellProvider;
import id.seria.itemseller.config.ConfigManager;
import id.seria.itemseller.config.MenuConfig;
import id.seria.itemseller.config.ItemPrice;
import id.seria.itemseller.gui.SellGui;
import id.seria.itemseller.gui.SellListener;
import id.seria.itemseller.manager.IntegrationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CyayoSell extends JavaPlugin implements CyayoSellAPI {
    private static CyayoSell instance;
    private ConfigManager configManager;
    private IntegrationManager integrationManager;
    private SellGui       sellGui;

    @Override
    public void onEnable() {
        instance = this;
        CyayoSellProvider.register(this);
        
        saveDefaultConfig();
        saveResource("menus/material.yml",       false);
        saveResource("menus/weapon.yml",          false);
        saveResource("items/material_prices.yml", false);
        saveResource("items/weapon_prices.yml",   false);

        configManager = new ConfigManager(this);
        configManager.load();
        integrationManager = new IntegrationManager(this);
        sellGui = new SellGui(this);
        getServer().getPluginManager().registerEvents(new SellListener(this), this);
        getCommand("itemsell").setTabCompleter(new ItemSellTabCompleter(this));
        getLogger().info("CyayoSell v1.2.0 aktif!");
    }

    @Override
    public void onDisable() { 
        CyayoSellProvider.unregister();
        getLogger().info("CyayoSell dinonaktifkan."); 
    }

    // --- API Implementation ---

    @Override
    public double getItemPrice(@NotNull ItemStack item) {
        String key = id.seria.itemseller.util.ItemHelper.getKey(item);
        if (key == null) return 0;
        ItemPrice price = configManager.getPrice(key);
        if (price == null) return 0;
        
        String quality = id.seria.itemseller.util.ItemHelper.getCustomQuality(item);
        return price.calculateUnitPrice(quality, configManager.getQualityAliases());
    }

    @Override
    public boolean canSellInMenu(@NotNull ItemStack item, @NotNull String menuKey) {
        String key = id.seria.itemseller.util.ItemHelper.getKey(item);
        if (key == null) return false;
        ItemPrice price = configManager.getPrice(key);
        return price != null && price.isAllowedInMenu(menuKey);
    }

    @Override
    public boolean openSellMenu(@NotNull Player player, @NotNull String menuKey) {
        MenuConfig menu = configManager.getMenu(menuKey);
        if (menu == null) return false;
        sellGui.open(player, menu);
        return true;
    }

    @Override
    @Nullable
    public MenuConfig getMenuConfig(@NotNull String menuKey) {
        return configManager.getMenu(menuKey);
    }

    private boolean isConsole(CommandSender s) { return s instanceof ConsoleCommandSender; }
    private boolean hasPerm(CommandSender s, String p) { return isConsole(s) || s.hasPermission(p); }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("itemsell")) return false;

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!hasPerm(sender, "itemsell.admin")) { sender.sendMessage(configManager.msg("no-permission")); return true; }
            configManager.load();
            sender.sendMessage(configManager.msg("reload-done"));
            return true;
        }

        if (args.length < 2) { sender.sendMessage(configManager.msg("usage")); return true; }

        String menuKey = args[0].toLowerCase();
        MenuConfig menu = configManager.getMenu(menuKey);
        if (menu == null) { sender.sendMessage(configManager.msg("menu-not-found", "{menu}", menuKey)); return true; }

        if (!hasPerm(sender, menu.getOpenPermission()) && !hasPerm(sender, "itemsell.admin")) {
            sender.sendMessage(configManager.msg(menu, "no-permission")); return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) { sender.sendMessage(configManager.msg(menu, "player-not-found")); return true; }

        sellGui.open(target, menu);
        if (!target.equals(sender))
            sender.sendMessage(configManager.msg(menu, "menu-open-notify",
                "{menu}", menu.getKey(), "{player}", target.getName()));
        return true;
    }

    public static CyayoSell getInstance() { return instance; }
    public ConfigManager getConfigManager()      { return configManager; }
    public IntegrationManager getIntegrationManager() { return integrationManager; }
    public SellGui       getSellGui()            { return sellGui; }
}
