package id.seria.itemseller.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import id.seria.itemseller.CyayoSell;

public class ConfigManager {
    private static final Pattern HEX = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    private static final List<Integer> DEFAULT_ITEM_SLOTS = Arrays.asList(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25
    );
    private final CyayoSell plugin;

    private String defaultPrefix, currencyId, currencyName;
    private List<String> sellMessageOrder = new ArrayList<>();
    private boolean estimateEnabled;
    private String estimateLoreLine, estimateNoItem;

    private final Map<String, String>     messages = new HashMap<>();
    private final Map<String, String>     sounds   = new HashMap<>();
    private final Map<String, String>     qualityAliases = new HashMap<>(); // LongString -> ShortKey
    private final Map<String, MenuConfig> menus    = new LinkedHashMap<>();
    private final Map<String, ItemPrice>  prices   = new LinkedHashMap<>();

    public ConfigManager(CyayoSell plugin) { this.plugin = plugin; }

    public void load() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();
        defaultPrefix  = cfg.getString("prefix",        "&7[&6ItemSeller&7]");
        currencyId     = cfg.getString("currency-id",   "gins");
        currencyName   = cfg.getString("currency-name", "Gins");
        sellMessageOrder = cfg.getStringList("sell-message-order");
        if (sellMessageOrder.isEmpty()) sellMessageOrder = Arrays.asList("header","item-lines","footer","total");
        estimateEnabled  = cfg.getBoolean("estimate-lore.enabled", true);
        estimateLoreLine = cfg.getString("estimate-lore.lore-line",    "&7Estimasi: &6{estimated} {currency}");
        estimateNoItem   = cfg.getString("estimate-lore.lore-no-item", "&7Belum ada item untuk dijual");
        loadSection(cfg, "messages", messages);
        loadSection(cfg, "sounds",   sounds);

        qualityAliases.clear();
        
        ConfigurationSection sSec = cfg.getConfigurationSection("quality-strings");
        if (sSec != null) {
            for (String shortKey : sSec.getKeys(false)) {
                String longString = sSec.getString(shortKey, "");
                if (!longString.isEmpty()) {
                    qualityAliases.put(longString, shortKey);
                }
            }
        }

        menus.clear();
        File menuDir = new File(plugin.getDataFolder(), "menus");
        if (!menuDir.exists()) menuDir.mkdirs();
        File[] mf = menuDir.listFiles();
        if (mf != null) { Arrays.sort(mf); for (File f : mf) { if (!f.getName().endsWith(".yml")) continue;
            String k = f.getName().replace(".yml","").toLowerCase();
            try { MenuConfig m = loadMenu(k, YamlConfiguration.loadConfiguration(f)); if (m != null) { menus.put(k, m); plugin.getLogger().info("Loaded menu: "+k); } }
            catch (Exception e) { plugin.getLogger().warning("Error menu "+f.getName()+": "+e.getMessage()); } } }

        prices.clear();
        File itemDir = new File(plugin.getDataFolder(), "items");
        if (!itemDir.exists()) itemDir.mkdirs();
        File[] ifiles = itemDir.listFiles();
        if (ifiles != null) { Arrays.sort(ifiles); for (File f : ifiles) { if (!f.getName().endsWith(".yml")) continue;
            try { loadItemFile(YamlConfiguration.loadConfiguration(f), f.getName()); }
            catch (Exception e) { plugin.getLogger().warning("Error items "+f.getName()+": "+e.getMessage()); } } }

        plugin.getLogger().info("Loaded "+menus.size()+" menus, "+prices.size()+" item prices.");
    }

    private void loadSection(FileConfiguration cfg, String section, Map<String,String> map) {
        map.clear();
        ConfigurationSection sec = cfg.getConfigurationSection(section);
        if (sec != null) for (String k : sec.getKeys(false)) map.put(k, sec.getString(k, ""));
    }

    private MenuConfig loadMenu(String key, FileConfiguration cfg) {
        String title    = color(cfg.getString("title", key));
        int    rows     = Math.max(3, Math.min(6, cfg.getInt("rows", 6)));
        String openPerm = cfg.getString("open-permission", "itemsell.admin");
        String rawPfx   = cfg.getString("prefix", null);
        String menuPfx  = (rawPfx != null && !rawPfx.isEmpty()) ? color(rawPfx) : null;

        String fillerMat  = cfg.getString("filler.material", "YELLOW_STAINED_GLASS_PANE");
        String fillerName = color(cfg.getString("filler.name", " "));
        int    fillerCmd  = cfg.getInt("filler.custom-model-data", 0);

        ConfigurationSection btn = cfg.getConfigurationSection("sell-button");
        int          btnSlot = btn != null ? btn.getInt("slot", 31)               : 31;
        String       btnMat  = btn != null ? btn.getString("material", "EMERALD")  : "EMERALD";
        String       btnName = color(btn != null ? btn.getString("name", "&a&lJUAL") : "&a&lJUAL");
        int          btnCmd  = btn != null ? btn.getInt("custom-model-data", 0)    : 0;
        List<String> btnLore = new ArrayList<>();
        if (btn != null) for (String l : btn.getStringList("lore")) btnLore.add(color(l));

        List<Integer> slots = new ArrayList<>();
        for (int s : cfg.getIntegerList("item-slots")) slots.add(s);
        if (slots.isEmpty()) {
            int size = rows * 9;
            for (int slot : DEFAULT_ITEM_SLOTS) if (slot < size && slot != btnSlot) slots.add(slot);
        }
        return new MenuConfig(key, title, rows, openPerm, menuPfx, slots, btnSlot,
            fillerMat, fillerName, fillerCmd, btnMat, btnName, btnLore, btnCmd);
    }

    private void loadItemFile(FileConfiguration cfg, String fileName) {
        for (Map<?,?> raw : cfg.getMapList("items")) {
            try {
                String type  = String.valueOf(raw.get("type")).toLowerCase();
                double price = Double.parseDouble(String.valueOf(raw.get("price")));
                List<String> allowed = new ArrayList<>();
                Object ra = raw.get("allowed-menus");
                if (ra instanceof List) for (Object o : (List<?>)ra) allowed.add(String.valueOf(o).toLowerCase());
                
                Map<String, Double> qualityPrices = new HashMap<>();
                Object rq = raw.get("quality-prices");
                if (rq instanceof Map) {
                    for (Map.Entry<?, ?> entry : ((Map<?, ?>) rq).entrySet()) {
                        qualityPrices.put(String.valueOf(entry.getKey()), Double.parseDouble(String.valueOf(entry.getValue())));
                    }
                }

                ItemPrice ip;
                if (type.equals("mmoitems"))
                    ip = ItemPrice.mmoitem(String.valueOf(raw.get("mmo-type")), String.valueOf(raw.get("mmo-id")), price, allowed, qualityPrices);
                else if (type.equals("vanilla"))
                    ip = ItemPrice.vanilla(String.valueOf(raw.get("material")), price, allowed);
                else continue;
                prices.put(ip.getKey(), ip);
            } catch (Exception e) { plugin.getLogger().warning("Error in "+fileName+": "+e.getMessage()); }
        }
    }

    public String color(String s) {
        return id.seria.itemseller.util.ColorUtil.color(s);
    }

    public String resolvePrefix(MenuConfig menu) {
        return (menu != null && menu.getPrefix() != null) ? menu.getPrefix() : color(defaultPrefix);
    }

    public String msg(MenuConfig menu, String key, String... kv) {
        String msg = messages.getOrDefault(key, "&c[?"+key+"]");
        for (int i = 0; i+1 < kv.length; i+=2) msg = msg.replace(kv[i], kv[i+1]);
        return color(resolvePrefix(menu) + " " + msg);
    }

    public String msg(String key, String... kv) { return msg(null, key, kv); }

    public String rawMsg(String key, String... kv) {
        String msg = messages.getOrDefault(key, "");
        for (int i = 0; i+1 < kv.length; i+=2) msg = msg.replace(kv[i], kv[i+1]);
        return color(msg);
    }

    public String sound(String key)              { return sounds.getOrDefault(key, ""); }
    public String getCurrencyId()                { return currencyId; }
    public String getCurrencyName()              { return currencyName; }
    public List<String> getSellMessageOrder()    { return sellMessageOrder; }
    public boolean isEstimateEnabled()           { return estimateEnabled; }
    public String getEstimateLoreLine()          { return color(estimateLoreLine); }
    public String getEstimateNoItem()            { return color(estimateNoItem); }
    public Map<String, MenuConfig> getMenus()    { return menus; }
    public MenuConfig getMenu(String key)        { return menus.get(key.toLowerCase()); }
    public ItemPrice getPrice(String key)        { return prices.get(key.toUpperCase()); }
    public Map<String, String> getQualityAliases()     { return qualityAliases; }
}
