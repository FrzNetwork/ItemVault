package io.github.lianjordaan.itemVault;

import io.github.lianjordaan.itemVault.subcommands.AddSubCommand;
import io.github.lianjordaan.itemVault.subcommands.DeleteSubCommand;
import io.github.lianjordaan.itemVault.subcommands.GetSubCommand;
import io.github.lianjordaan.itemVault.subcommands.ListSubCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MultiItemsCommand implements CommandExecutor {
    private final ItemVault plugin;
    private final AddSubCommand addSubCommand;
    private final DeleteSubCommand deleteSubCommand;
    private final GetSubCommand getSubCommand;
    private final ListSubCommand listSubCommand;

    MultiItemsCommand(ItemVault plugin) {
        this.plugin = plugin;
        this.addSubCommand = new AddSubCommand(plugin);
        this.deleteSubCommand = new DeleteSubCommand(plugin);
        this.getSubCommand = new GetSubCommand(plugin);
        this.listSubCommand = new ListSubCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isForceHelpEnabled = plugin.getConfig().getBoolean("itemvault.force-help", true);
        String invalidArgumentsMessage = plugin.getConfig().getString("messages.invalid-arguments", "<red>Invalid arguments. Use /itemvault help for more information.");

        if (args.length == 0) {
            if (isForceHelpEnabled) {
                if (sender instanceof Player) {
                    ((Player) sender).performCommand("itemvault help");
                } else {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
                }
            } else {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
            }
            return true;
        }

        switch (args[0]) {
            case "add":
                return addSubCommand.execute(sender, command, label, args);
            case "delete":
                return deleteSubCommand.execute(sender, command, label, args);
            case "give":
            case "get":
                return getSubCommand.execute(sender, command, label, args);
            case "list":
                return listSubCommand.execute(sender, command, label, args);
            case "reload":
                if (args.length == 1) {
                    plugin.reloadConfig();
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Items reloaded."));
                    return true;
                } else {
                    if (isForceHelpEnabled) {
                        if (sender instanceof Player) {
                            ((Player) sender).performCommand("itemvault help");
                        } else {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
                        }
                    } else {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
                    }
                    return true;
                }
            case "help":
                if (args.length == 1) {
                    List<String> helpMessages;
                    helpMessages = plugin.getConfig().getStringList("messages.help-messages");
                    for (String message : helpMessages) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
                    }
                    return true;
                } else {
                    if (isForceHelpEnabled) {
                        if (sender instanceof Player) {
                            ((Player) sender).performCommand("itemvault help");
                        } else {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
                        }
                    } else {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
                    }
                    return true;
                }
            default:
                if (isForceHelpEnabled) {
                    if (sender instanceof Player) {
                        ((Player) sender).performCommand("itemvault help");
                    } else {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
                    }
                } else {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
                }
                return true;
        }

    }
}
