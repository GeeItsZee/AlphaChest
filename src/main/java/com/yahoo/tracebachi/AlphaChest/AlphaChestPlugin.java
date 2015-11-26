package com.yahoo.tracebachi.AlphaChest;

import com.yahoo.tracebachi.AlphaChest.commands.ChestCommands;
import com.yahoo.tracebachi.AlphaChest.commands.WorkbenchCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class AlphaChestPlugin extends JavaPlugin implements Listener
{
    private VirtualChestManager chestManager;
    private boolean clearOnDeath;
    private boolean dropOnDeath;

    @Override
    public void onLoad()
    {
        File config = new File(getDataFolder(), "config.yml");
        if(!config.exists())
        {
            saveDefaultConfig();
        }

        new File(getDataFolder(), "chests").mkdir();
    }

    public void onEnable()
    {
        reloadConfig();
        clearOnDeath = getConfig().getBoolean("clearOnDeath");
        dropOnDeath = getConfig().getBoolean("dropOnDeath");

        File chestFolder = new File(getDataFolder(), "chests");
        chestManager = new VirtualChestManager(chestFolder, getLogger());

        ChestCommands chestCommands = new ChestCommands(chestManager);
        getCommand("chest").setExecutor(chestCommands);
        getCommand("clearchest").setExecutor(chestCommands);
        getCommand("savechests").setExecutor(chestCommands);
        getCommand("workbench").setExecutor(new WorkbenchCommand());

        getServer().getPluginManager().registerEvents(this, this);

        int autosaveInterval = getConfig().getInt("autosave") * 1200;
        if(autosaveInterval > 0)
        {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> chestManager.saveAll(),
                autosaveInterval, autosaveInterval);
        }
    }

    public void onDisable()
    {
        chestManager.saveAll();
        chestManager = null;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if(player.hasPermission("alphachest.chest"))
        {
            chestManager.save(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        boolean drop = dropOnDeath;
        boolean clear = drop || clearOnDeath;

        if(player.hasPermission("alphachest.keepOnDeath"))
        {
            drop = false;
            clear = false;
        }
        else if(player.hasPermission("alphachest.dropOnDeath"))
        {
            drop = true;
            clear = true;
        }
        else if(player.hasPermission("alphachest.clearOnDeath"))
        {
            drop = false;
            clear = true;
        }

        if(drop)
        {
            List<ItemStack> drops = event.getDrops();
            Inventory chest = chestManager.getChest(player.getName());
            for(int i = 0; i < chest.getSize(); i++)
            {
                drops.add(chest.getItem(i));
            }
        }

        if(clear)
        {
            chestManager.removeChest(player.getName());
        }
    }
}
