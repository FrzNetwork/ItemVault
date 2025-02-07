package io.github.lianjordaan.itemVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MultiItemsTabCompletion implements TabCompleter {
    private final ItemVault plugin;

    public MultiItemsTabCompletion(ItemVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(List.of("add", "delete", "give", "get", "list", "reload"));
        } else if (args.length == 2) {
            switch (args[0]) {
                case "delete":
                case "get":
                case "give":
                    completions.addAll(new ItemManager().getAllItemsList());
                    break;
                case "add":
                    completions.addAll(new ItemManager().getAllItemFolders()); // Use new function
                    break;
            }
        }

        // Return matching completions (filter by user input)
        return filterCompletions(completions, args);
    }



    // Filters completions based on user input
    private List<String> filterCompletions(List<String> completions, String[] args) {
        String currentInput = args[args.length - 1].toLowerCase();
        List<String> filtered = new ArrayList<>();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(currentInput)) {
                filtered.add(completion);
            }
        }
        return filtered;
    }
}
