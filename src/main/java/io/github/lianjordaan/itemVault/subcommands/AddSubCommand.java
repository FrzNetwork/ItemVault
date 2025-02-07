package io.github.lianjordaan.itemVault.subcommands;

import io.github.lianjordaan.itemVault.ItemVault;
import io.github.lianjordaan.itemVault.ItemManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AddSubCommand {
    private final ItemVault plugin;

    public AddSubCommand(ItemVault plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "messages.add-console-cannot-add-items", "<red>Only players can add items.");
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
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            sendMessage(sender, "messages.add-no-item-in-hand", "<red>You need to hold an item in your hand to add it.");
            return true;
        }

        if (existingItemCheck != null) {
            boolean allowOverride = plugin.getConfig().getBoolean("itemvault.allow-override", false);
            if (!allowOverride) {
                sendMessageWithItemId(sender, "messages.add-item-already-exists", "<red>Item already exists.", id);
                return true;
            }

            if (itemManager.saveItem(item, id)) {
                boolean giveItemOnOverwrite = plugin.getConfig().getBoolean("itemvault.give-item-on-overwrite", false);
                if (giveItemOnOverwrite) {
                    player.getInventory().addItem(existingItemCheck);
                    sendMessageWithItemId(sender, "messages.add-item-added-and-overriden-given",
                            "<yellow>An item with ID <gold>%item_id% <yellow>already existed. It was overridden, but the old item was given to you.", id);
                } else {
                    sendMessageWithItemId(sender, "messages.add-item-added-and-overriden",
                            "<yellow>An item with ID <gold>%item_id% <yellow>already existed and was overridden.", id);
                }
                return true;
            }
        }

        if (itemManager.saveItem(item, id)) {
            sendMessageWithItemId(sender, "messages.add-item-added", "<green>Item has been added with ID <gold>%item_id%", id);
        } else {
            sendMessage(sender, "messages.add-error-adding-item", "<red>Error adding item.");
        }
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
