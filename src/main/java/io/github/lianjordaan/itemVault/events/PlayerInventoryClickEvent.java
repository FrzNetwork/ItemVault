package io.github.lianjordaan.itemVault.events;

import io.github.lianjordaan.itemVault.ItemVault;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerInventoryClickEvent implements Listener {
    private final ItemVault plugin;

    public PlayerInventoryClickEvent(ItemVault plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        NamespacedKey openFolderKey = new NamespacedKey(plugin, "open_folder");
        NamespacedKey cancelClickKey = new NamespacedKey(plugin, "cancel_click");
        NamespacedKey giveItemKey = new NamespacedKey(plugin, "give_item");
        NamespacedKey pageKey = new NamespacedKey(plugin, "page");

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(cancelClickKey, PersistentDataType.BOOLEAN)) {
            if (Boolean.TRUE.equals(container.get(cancelClickKey, PersistentDataType.BOOLEAN))) {
                event.setCancelled(true);
            }
        }

        if (container.has(openFolderKey, PersistentDataType.STRING) && container.has(pageKey, PersistentDataType.INTEGER)) {
            String path = container.get(openFolderKey, PersistentDataType.STRING);
            Integer page = container.get(pageKey, PersistentDataType.INTEGER);
            player.performCommand("itemvault list " + path + " " + page);
        }

        if (container.has(giveItemKey, PersistentDataType.STRING)) {
            String id = container.get(giveItemKey, PersistentDataType.STRING);
            player.performCommand("itemvault give " + id);
        }
    }
}
