package io.github.lianjordaan.itemVault.subcommands;

import io.github.lianjordaan.itemVault.ItemVault;
import io.github.lianjordaan.itemVault.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListSubCommand {
    private final ItemVault plugin;

    public ListSubCommand(ItemVault plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 3) {
            sendInvalidArgsMessage(sender);
            return true;
        }

        if (!(sender instanceof Player)) {
            String message = plugin.getConfig().getString("messages.list-console-cannot-list-items", "<red>Only players can list items.");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
            return true;
        }

        Player player = (Player) sender;

        String path = args.length >= 2 ? args[1] : "/";
        int page = 1;

        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sendInvalidArgsMessage(sender);
                return true;
            }
        }

        ItemManager itemManager = new ItemManager(plugin);
        List<String> itemList = itemManager.getItemList(path);

        if (itemList.isEmpty()) {
            sendMessageWithPath(sender, "messages.list-no-items-found-path", "<red>No items found in <gold>%path%", path);
            return true;
        }

        // Constants for pagination
        int ITEMS_PER_PAGE = 1*9; // Adjust this to your preferred number of items per page
        int totalItems = itemList.size();
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

        // Ensure the requested page is valid
        if (page < 1 || page > totalPages) {
            sendMessage(sender, "messages.list-invalid-page", "<red>Invalid page number. There are only <gold>" + totalPages + "<red> pages.");
            return true;
        }

        // Calculate the items to display on the current page
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
        List<String> pageItems = itemList.stream().sorted().toList().subList(startIndex, endIndex);

        // Create an inventory
        String inventoryTitle = plugin.getConfig().getString("itemvault.list-inventory-title", "<green>ItemVault %page%/%max_page% | <aqua>%path%");
        inventoryTitle = inventoryTitle.replace("%page%", String.valueOf(page));
        inventoryTitle = inventoryTitle.replace("%max_page%", String.valueOf(totalPages));
        inventoryTitle = inventoryTitle.replace("%path%", path);

        Inventory inventory = Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize(inventoryTitle));

        for (int i = 0; i < 54; i++) {
            ItemStack emptyItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = emptyItem.getItemMeta();
            meta.setHideTooltip(true);

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "cancel_click"), PersistentDataType.BOOLEAN, true);

            emptyItem.setItemMeta(meta);

            inventory.setItem(i, emptyItem);
        }

        for (int i = 0; i < 9; i++) {
            ItemStack emptyItem = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
            ItemMeta meta = emptyItem.getItemMeta();
            meta.setHideTooltip(true);

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "cancel_click"), PersistentDataType.BOOLEAN, true);

            emptyItem.setItemMeta(meta);

            inventory.setItem(i + 5*9, emptyItem);
        }

        for (int i = 0; i < pageItems.size(); i++) {
            String itemName = pageItems.get(i);
            ItemStack item = new ItemStack(Material.AIR);
            if (itemName.endsWith(".folder")) {
                item = new ItemStack(Material.CHEST);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(MiniMessage.miniMessage().deserialize("<!i><gold>Folder"));
                meta.lore(List.of(MiniMessage.miniMessage().deserialize("<!i><white>Click to open folder <gold>" + path + itemName.replace(".folder", "") + "/</gold>")));

                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(new NamespacedKey(plugin, "open_folder"), PersistentDataType.STRING, path + itemName.replace(".folder", "") + "/");
                container.set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, 1);
                container.set(new NamespacedKey(plugin, "cancel_click"), PersistentDataType.BOOLEAN, true);

                item.setItemMeta(meta);
            } else if (itemName.endsWith(".empty")) {
                item = new ItemStack(Material.ENDER_CHEST);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(MiniMessage.miniMessage().deserialize("<!i><gray>Folder <gold>" + path + itemName.replace(".empty", "") + "</gold> is empty"));

                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(new NamespacedKey(plugin, "cancel_click"), PersistentDataType.BOOLEAN, true);

                item.setItemMeta(meta);
            } else if (itemName.endsWith(".iv")) {
                item = itemManager.getItem(path + "/" + itemName.replace(".iv", ""));
                List<Component> lore = item.lore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                lore.add(MiniMessage.miniMessage().deserialize(" "));
                lore.add(MiniMessage.miniMessage().deserialize("<!i><gold><u>Name:</u> <white>" + itemName.replace(".iv", "")));
                item.lore(lore);
                ItemMeta meta = item.getItemMeta();

                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(new NamespacedKey(plugin, "give_item"), PersistentDataType.STRING, path + itemName.replace(".iv", ""));
                container.set(new NamespacedKey(plugin, "cancel_click"), PersistentDataType.BOOLEAN, true);

                item.setItemMeta(meta);
            }
            inventory.setItem(i, item);
        }

        if (page != 1) {
            ItemStack backButton = new ItemStack(Material.TIPPED_ARROW);
            ItemMeta meta = backButton.getItemMeta();
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setColor(Color.fromRGB(0xFF0000));
            potionMeta.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Previous Page"));

            PersistentDataContainer container = potionMeta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "open_folder"), PersistentDataType.STRING, path);
            container.set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, page - 1);
            container.set(new NamespacedKey(plugin, "cancel_click"), PersistentDataType.BOOLEAN, true);

            backButton.setItemMeta(potionMeta);
            inventory.setItem(0 + 5*9, backButton);
        }

        if (page < totalPages) {
            ItemStack nextButton = new ItemStack(Material.TIPPED_ARROW);
            ItemMeta meta = nextButton.getItemMeta();
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setColor(Color.fromRGB(0x00FF00));
            potionMeta.displayName(MiniMessage.miniMessage().deserialize("<!i><green>Next Page"));

            PersistentDataContainer container = potionMeta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "open_folder"), PersistentDataType.STRING, path);
            container.set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, page + 1);
            container.set(new NamespacedKey(plugin, "cancel_click"), PersistentDataType.BOOLEAN, true);

            nextButton.setItemMeta(potionMeta);
            inventory.setItem(8 + 5*9, nextButton);
        }

        if (!path.equals("/")) {
            ItemStack backButton = new ItemStack(Material.SPECTRAL_ARROW);
            ItemMeta meta = backButton.getItemMeta();
            meta.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Back"));

            PersistentDataContainer container = meta.getPersistentDataContainer();
            String newPath = path.substring(0, path.replaceAll("/[^/]*$", "").lastIndexOf("/"));
            newPath = newPath + "/";
            container.set(new NamespacedKey(plugin, "open_folder"), PersistentDataType.STRING, newPath);
            container.set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, 1);
            container.set(new NamespacedKey(plugin, "cancel_click"), PersistentDataType.BOOLEAN, true);

            backButton.setItemMeta(meta);
            inventory.setItem(4 + 5*9, backButton);
        }

        player.openInventory(inventory);

        return true;
    }

    private void sendMessage(CommandSender sender, String configKey, String defaultMsg) {
        String message = plugin.getConfig().getString(configKey, defaultMsg);
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    private void sendMessageWithPath(CommandSender sender, String configKey, String defaultMsg, String path) {
        String message = plugin.getConfig().getString(configKey, defaultMsg);
        message = message.replace("%path%", path);
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
