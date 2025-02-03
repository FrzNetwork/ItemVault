package io.github.lianjordaan.itemVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class MultiItemsTabCompletion implements TabCompleter {
    private final ItemVault plugin;

    public MultiItemsTabCompletion(ItemVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("add", "delete", "give", "get", "list", "reload");
        } else if (args.length == 2) {
            switch (args[0]) {
                case "delete":
                    return new ItemManager(plugin).getAllItemsList();
                case "get":
                case "give":
                    return new ItemManager(plugin).getAllItemsList();
                default:
                    return List.of();
            }
        } else {
            return List.of();
        }
    }
}
