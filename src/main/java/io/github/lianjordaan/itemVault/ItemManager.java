package io.github.lianjordaan.itemVault;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ItemManager {

    private final JavaPlugin plugin;

    public ItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Save the item to a file
    public boolean saveItem(ItemStack item, String id) {
        // Replace "/" with the file separator and build the folder path
        String filePath = plugin.getDataFolder() + File.separator + "items" + File.separator + id.replaceAll("/", File.separator) + ".iv";

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
    public ItemStack getItem(String id) {
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
//            e.printStackTrace();
            return null;
        }
    }

    // Delete the item from a file
    public boolean deleteItem(String id) {
        // Replace "/" with the file separator and build the folder path
        String filePath = plugin.getDataFolder() + File.separator + "items" + File.separator + id.replace("/", File.separator) + ".iv";

        try {
            // Delete the file
            Files.deleteIfExists(Paths.get(filePath));

            return true;
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
    }

    public List<String> getItemList(String path) {
        List<String> itemList = new ArrayList<>();
        File baseFolder = new File(plugin.getDataFolder(), "items");

        // Resolve the actual directory based on input path
        File targetFolder = path.isEmpty() ? baseFolder : new File(baseFolder, path.replace("/", File.separator));

        if (!targetFolder.exists() || !targetFolder.isDirectory()) {
            return itemList; // Return empty list if path doesn't exist
        }

        File[] files = targetFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Check if the directory is empty (has no .iv files or subfolders)
                    File[] innerFiles = file.listFiles((dir, name) -> name.endsWith(".iv") || new File(dir, name).isDirectory());

                    if (innerFiles == null || innerFiles.length == 0) {
                        itemList.add(file.getName() + ".empty"); // Mark empty folders
                    } else {
                        itemList.add(file.getName() + ".folder"); // Add folder normally
                    }
                } else if (file.isFile() && file.getName().endsWith(".iv")) {
                    itemList.add(file.getName()); // Keep .iv for files
                }
            }
        }
        return itemList;
    }

    public List<String> getAllItemsList() {
        List<String> itemList = new ArrayList<>();
        File baseFolder = new File(plugin.getDataFolder(), "items");

        if (!baseFolder.exists() || !baseFolder.isDirectory()) {
            return itemList; // Return empty list if no items exist
        }

        // Recursively collect all items
        collectItemsRecursively(baseFolder, "", itemList);
        return itemList;
    }

    private void collectItemsRecursively(File folder, String pathPrefix, List<String> itemList) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    collectItemsRecursively(file, pathPrefix + file.getName() + "/", itemList);
                } else if (file.isFile() && file.getName().endsWith(".iv")) {
                    String itemName = file.getName().substring(0, file.getName().length() - 3); // Remove ".iv"
                    itemList.add(pathPrefix + itemName);
                }
            }
        }
    }

    public List<String> getAllItemFolders() {
        List<String> folderList = new ArrayList<>();
        File baseFolder = new File(plugin.getDataFolder(), "items");

        if (baseFolder.exists() && baseFolder.isDirectory()) {
            collectFolders(baseFolder, "", folderList);
        }

        return folderList;
    }

    // Recursive function to collect non-empty folders
    private void collectFolders(File folder, String relativePath, List<String> folderList) {
        File[] files = folder.listFiles();
        if (files == null) return;

        boolean hasIVFiles = false;
        for (File file : files) {
            if (file.isDirectory()) {
                collectFolders(file, relativePath + file.getName() + "/", folderList);
            } else if (file.isFile() && file.getName().endsWith(".iv")) {
                hasIVFiles = true;
            }
        }

        if (hasIVFiles) {
            folderList.add(relativePath.isEmpty() ? "/" : relativePath);
        }
    }



}
