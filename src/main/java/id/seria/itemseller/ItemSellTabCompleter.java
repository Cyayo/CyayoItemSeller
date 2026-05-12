package id.seria.itemseller;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemSellTabCompleter implements TabCompleter {
    private final CyayoSell plugin;
    public ItemSellTabCompleter(CyayoSell plugin) { this.plugin = plugin; }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        boolean console = !(sender instanceof Player);

        if (args.length == 1) {
            String pref = args[0].toLowerCase();
            for (String k : plugin.getConfigManager().getMenus().keySet()) {
                id.seria.itemseller.config.MenuConfig m = plugin.getConfigManager().getMenu(k);
                if (m == null) continue;
                boolean can = console || sender.hasPermission("itemsell.admin") || sender.hasPermission(m.getOpenPermission());
                if (can && k.startsWith(pref)) result.add(k);
            }
            if ((console || sender.hasPermission("itemsell.admin")) && "reload".startsWith(pref)) result.add("reload");
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("reload")) {
            String pref = args[1].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().toLowerCase().startsWith(pref)) result.add(p.getName());
        }
        return result;
    }
}
