/*
 * This file is part of AlphaChest.
 *
 * AlphaChest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AlphaChest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AlphaChest.  If not, see <http://www.gnu.org/licenses/>.
 */
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
