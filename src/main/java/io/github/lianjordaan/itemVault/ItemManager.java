package io.github.lianjordaan.itemVault;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ItemManager {

    private final JavaPlugin plugin;

    public ItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Save the item to a file
    public boolean saveItem(ItemStack item, String id) {
        // Replace "/" with the file separator and build the folder path
        String filePath = plugin.getDataFolder() + File.separator + "items" + File.separator + id.replace("/", File.separator) + ".iv";

        try {
            // Create necessary directories
            Files.createDirectories(Paths.get(filePath).getParent());

            // Serialize the item to bytes
            byte[] itemBytes = item.serializeAsBytes();

            // Encode the bytes to base64
            String encodedItem = Base64.getEncoder().encodeToString(itemBytes);

            // Write the encoded string to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(encodedItem);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Load the item from a file
    public ItemStack loadItem(String id) {
        // Replace "/" with the file separator and build the folder path
        String filePath = plugin.getDataFolder() + File.separator + "items" + File.separator + id.replace("/", File.separator) + ".iv";

        try {
            // Read the encoded string from the file
            String encodedItem = new String(Files.readAllBytes(Paths.get(filePath)));

            // Decode the base64 string
            byte[] itemBytes = Base64.getDecoder().decode(encodedItem);

            // Deserialize the bytes back into an ItemStack
            return ItemStack.deserializeBytes(itemBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
