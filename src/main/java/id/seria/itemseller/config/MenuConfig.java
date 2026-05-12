package id.seria.itemseller.config;

import java.util.List;

public class MenuConfig {
    private final String key, title, openPermission, prefix;
    private final int rows, sellButtonSlot, fillerCmd, sellBtnCmd;
    private final String fillerMaterial, fillerName, sellBtnMaterial, sellBtnName;
    private final List<String> sellBtnLore;
    private final List<Integer> itemSlots;

    public MenuConfig(String key, String title, int rows, String openPermission, String prefix,
                      List<Integer> itemSlots, int sellButtonSlot,
                      String fillerMaterial, String fillerName, int fillerCmd,
                      String sellBtnMaterial, String sellBtnName, List<String> sellBtnLore, int sellBtnCmd) {
        this.key = key; this.title = title; this.rows = rows;
        this.openPermission = openPermission; this.prefix = prefix;
        this.itemSlots = itemSlots; this.sellButtonSlot = sellButtonSlot;
        this.fillerMaterial = fillerMaterial; this.fillerName = fillerName; this.fillerCmd = fillerCmd;
        this.sellBtnMaterial = sellBtnMaterial; this.sellBtnName = sellBtnName;
        this.sellBtnLore = sellBtnLore; this.sellBtnCmd = sellBtnCmd;
    }

    public String       getKey()             { return key; }
    public String       getTitle()           { return title; }
    public int          getRows()            { return rows; }
    public String       getOpenPermission()  { return openPermission; }
    public String       getPrefix()          { return prefix; }
    public List<Integer> getItemSlots()      { return itemSlots; }
    public int          getSellButtonSlot()  { return sellButtonSlot; }
    public String       getFillerMaterial()  { return fillerMaterial; }
    public String       getFillerName()      { return fillerName; }
    public int          getFillerCmd()       { return fillerCmd; }
    public String       getSellBtnMaterial() { return sellBtnMaterial; }
    public String       getSellBtnName()     { return sellBtnName; }
    public List<String> getSellBtnLore()     { return sellBtnLore; }
    public int          getSellBtnCmd()      { return sellBtnCmd; }
}
