package io.github.lianjordaan.itemVault.subcommands;

import io.github.lianjordaan.itemVault.ItemManager;
import io.github.lianjordaan.itemVault.ItemVault;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetSubCommand {
    private final ItemVault plugin;

    public GetSubCommand(ItemVault plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "messages.get-console-cannot-get-items", "<red>Only players can get items.");
            return true;
        }

        if (args.length != 2) {
            sendInvalidArgsMessage(sender);
            return true;
        }

        Player player = (Player) sender;
        ItemManager itemManager = new ItemManager();
        String id = args[1];
        ItemStack existingItemCheck = itemManager.getItem(id);

        if (existingItemCheck != null) {
            player.getInventory().addItem(existingItemCheck);
            sendMessageWithItemId(sender, "messages.get-item-given", "<green>Item with ID <gold>%item_id% <green>has been given to you.", id);
            return true;
        }

        sendMessageWithItemId(sender, "messages.get-item-not-found", "<red>Item with ID <gold>%item_id% <red>doesn't exist.", id);
        return true;
    }

    private void sendMessage(CommandSender sender, String configKey, String defaultMsg) {
        String message = plugin.getConfig().getString(configKey, defaultMsg);
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
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
