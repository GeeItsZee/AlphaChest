package com.yahoo.tracebachi.AlphaChest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class VirtualChestManager
{
    private final File dataFolder;
    private final Logger logger;
    private final HashMap<String, Inventory> chests = new HashMap<>();;

    public VirtualChestManager(File dataFolder, Logger logger)
    {
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    private boolean load(String playerName)
    {
        File chestFile = new File(dataFolder, playerName.toLowerCase() + ".chest.yml");

        if(chestFile.exists())
        {
            try
            {
                Inventory chest = InventoryIO.loadFromYaml(chestFile, playerName);
                chests.put(playerName.toLowerCase(), chest);
                return true;
            }
            catch(IOException | InvalidConfigurationException e)
            {
                logger.warning("Failed to load chest for " + playerName);
                e.printStackTrace();
            }
        }

        return false;
    }

    public void save(String playerName)
    {
        File chestFile = new File(dataFolder, playerName.toLowerCase() + ".chest.yml");
        Inventory chest = chests.remove(playerName.toLowerCase());

        // If chest is not in map, there's no point in loading and saving an unchanged file
        if(chest == null) { return; }

        try
        {
            InventoryIO.saveToYaml(chest, chestFile);
        }
        catch(IOException e)
        {
            logger.warning("Failed to save chest for " + playerName);
            e.printStackTrace();
        }
    }

    public void saveAll()
    {
        for(Map.Entry<String, Inventory> entry : chests.entrySet())
        {
            String playerName = entry.getKey();
            Inventory chest = entry.getValue();

            File chestFile = new File(dataFolder, playerName.toLowerCase() + ".chest.yml");

            try
            {
                InventoryIO.saveToYaml(chest, chestFile);
            }
            catch(IOException e)
            {
                logger.warning("Failed to save chest for " + playerName);
                e.printStackTrace();
            }
        }

        logger.info("Auto-Saved " + chests.size() + " chests.");
    }

    public Inventory getChest(String playerName)
    {
        Inventory chest = chests.get(playerName.toLowerCase());
        if(chest != null)
        {
            return chest;
        }

        // Try to load the chest from disk
        load(playerName);
        return chests.get(playerName.toLowerCase());
    }

    public Inventory createChest(String playerName)
    {
        Inventory chest = Bukkit.getServer().createInventory(null, 54,
            "AlphaChest - " + playerName.toLowerCase());
        chests.put(playerName.toLowerCase(), chest);
        return chest;
    }

    public void removeChest(String playerName)
    {
        // Remove from map
        chests.remove(playerName.toLowerCase());

        // Remove the disk
        File chestFile = new File(dataFolder, playerName.toLowerCase() + ".chest.yml");
        if(chestFile.exists())
        {
            chestFile.delete();
        }
    }
}
