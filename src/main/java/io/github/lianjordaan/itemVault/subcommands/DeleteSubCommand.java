package io.github.lianjordaan.itemVault.subcommands;

import io.github.lianjordaan.itemVault.ItemManager;
import io.github.lianjordaan.itemVault.ItemVault;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeleteSubCommand {

    private final ItemVault plugin;

    public DeleteSubCommand(ItemVault plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sendInvalidArgsMessage(sender);
            return true;
        }

        ItemManager itemManager = new ItemManager(plugin);
        String id = args[1];
        ItemStack item = itemManager.getItem(id);

        if (item == null) {
            sendMessageWithItemId(sender, "messages.delete-item-not-found", "<red>Item not found.", id);
            return true;
        }

        if (!(sender instanceof Player)) {
            boolean giveItemOnDelete = plugin.getConfig().getBoolean("itemvault.give-item-on-delete", true);
            if (giveItemOnDelete) {
                sendMessageWithItemId(sender, "messages.delete-console-cannot-delete-items",
                        "<red>Console cannot delete items when give-item-on-delete is enabled.", id);
                return true;
            }
        }

        if (itemManager.deleteItem(id)) {
            boolean giveItemOnDelete = plugin.getConfig().getBoolean("itemvault.give-item-on-delete", true);

            if (giveItemOnDelete) {
                if (!(sender instanceof Player)) {
                    sendMessageWithItemId(sender, "messages.delete-console-cannot-delete-items",
                            "<red>Console cannot delete items when give-item-on-delete is enabled.", id);
                    return true;
                }
                Player player = (Player) sender;
                player.getInventory().addItem(item);
                sendMessageWithItemId(sender, "messages.delete-item-deleted-and-given",
                        "<green>Item with ID <gold>%item_id% <green>has been deleted and given to you.", id);
            } else {
                sendMessageWithItemId(sender, "messages.delete-item-deleted",
                        "<green>Item with ID <gold>%item_id% <green>has been deleted.", id);
            }
        } else {
            sendMessageWithItemId(sender, "messages.delete-error-deleting-item",
                    "<red>Error deleting item with ID <gold>%item_id%", id);
        }
        return true;
    }

    private void sendMessageWithItemId(CommandSender sender, String configKey, String defaultMsg, String itemId) {
        String message = plugin.getConfig().getString(configKey, defaultMsg).replace("%item_id%", itemId);
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    private void sendInvalidArgsMessage(CommandSender sender) {
        boolean isForceHelpEnabled = plugin.getConfig().getBoolean("itemvault.force-help-on-invalid", true);
        String invalidArgumentsMessage = plugin.getConfig().getString("messages.invalid-arguments", "<red>Invalid arguments. Use /itemvault help.");

        if (isForceHelpEnabled) {
            if (sender instanceof Player) {
                ((Player) sender).performCommand("itemvault help");
            } else {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
            }
        } else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalidArgumentsMessage));
        }
    }
}
