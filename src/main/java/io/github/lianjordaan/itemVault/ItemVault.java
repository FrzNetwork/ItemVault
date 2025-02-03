package io.github.lianjordaan.itemVault;

import io.github.lianjordaan.itemVault.events.PlayerInventoryClickEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemVault extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        // register commands
        this.getCommand("itemvault").setExecutor(new MultiItemsCommand(this));
        // register events
        this.getServer().getPluginManager().registerEvents(new PlayerInventoryClickEvent(this), this);
        // register tab completion
        this.getCommand("itemvault").setTabCompleter(new MultiItemsTabCompletion(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
