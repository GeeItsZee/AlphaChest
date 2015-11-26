package com.yahoo.tracebachi.AlphaChest;

import com.yahoo.tracebachi.AlphaChest.commands.ChestCommands;
import com.yahoo.tracebachi.AlphaChest.commands.WorkbenchCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class AlphaChestPlugin extends JavaPlugin implements Listener
{
    public static final String CHEST_PREFIX = "AlphaChest - ";

    private File chestFolder;

    @Override
    public void onLoad()
    {
        chestFolder = new File(getDataFolder(), "chests");
        chestFolder.mkdir();
    }

    public void onEnable()
    {
        ChestCommands chestCommands = new ChestCommands(chestFolder, getLogger());
        getCommand("chest").setExecutor(chestCommands);
        getCommand("clearchest").setExecutor(chestCommands);
        getCommand("workbench").setExecutor(new WorkbenchCommand());

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        Inventory inventory = event.getInventory();
        String title = inventory.getTitle();

        if(title != null && title.startsWith(CHEST_PREFIX))
        {
            String playerName = title.substring(CHEST_PREFIX.length());
            File file = new File(chestFolder, playerName.toLowerCase() + ".chest.yml");

            try
            {
                InventoryIO.saveToYaml(inventory, file);
            }
            catch(IOException e)
            {
                getLogger().info("Failed to load chest for " + playerName);
                e.printStackTrace();
            }
        }
    }
}
