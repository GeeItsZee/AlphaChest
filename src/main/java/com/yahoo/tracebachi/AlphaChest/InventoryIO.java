package com.yahoo.tracebachi.AlphaChest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public interface InventoryIO
{
    static Inventory loadFromYaml(File file, String playerName) throws IOException, InvalidConfigurationException
    {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        int inventorySize = yaml.getInt("size", 54);
        ConfigurationSection items = yaml.getConfigurationSection("items");
        Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize,
            AlphaChestPlugin.CHEST_PREFIX + playerName.toLowerCase());

        for(int slot = 0; slot < inventorySize; slot++)
        {
            String slotString = Integer.toString(slot);
            if(items.isItemStack(slotString))
            {
                ItemStack itemStack = items.getItemStack(slotString);
                inventory.setItem(slot, itemStack);
            }
        }

        return inventory;
    }

    static void saveToYaml(Inventory inventory, File file) throws IOException
    {
        YamlConfiguration yaml = new YamlConfiguration();

        int inventorySize = inventory.getSize();
        yaml.set("size", inventorySize);
        ConfigurationSection items = yaml.createSection("items");

        for(int slot = 0; slot < inventorySize; slot++)
        {
            ItemStack stack = inventory.getItem(slot);
            if(stack != null)
            {
                items.set(Integer.toString(slot), stack);
            }
        }

        yaml.save(file);
    }
}
