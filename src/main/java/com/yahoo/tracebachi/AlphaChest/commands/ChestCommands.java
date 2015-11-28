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
package com.yahoo.tracebachi.AlphaChest.commands;

import com.yahoo.tracebachi.AlphaChest.InventoryIO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ChestCommands implements CommandExecutor
{
    private static final String SUCCESS = ChatColor.translateAlternateColorCodes('&', "&8[&aAlphaChest&8]&a ");
    private static final String ERROR = ChatColor.translateAlternateColorCodes('&', "&8[&aAlphaChest&8]&c ");

    private final File chestFolder;
    private final Logger logger;

    public ChestCommands(File chestFolder, Logger logger)
    {
        this.chestFolder = chestFolder;
        this.logger = logger;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        String name = command.getName();
        if(name.equals("chest"))
        {
            return performChestCommand(sender, args);
        }
        else if(name.equalsIgnoreCase("clearchest"))
        {
            return performClearChestCommand(sender, args);
        }
        return false;
    }

    private boolean performChestCommand(CommandSender sender, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ERROR + "Command can only be performed by a player.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();
        GameMode mode = player.getGameMode();

        if(mode == GameMode.CREATIVE && !player.hasPermission("alphachest.chest.creativeMode"))
        {
            player.sendMessage(ERROR + "You are in creative, but you don't have permission to use /chest in creative.");
            return true;
        }

        if(args.length == 0)
        {
            if(!player.hasPermission("alphachest.chest"))
            {
                player.sendMessage(ERROR + "You don't have permission to use /chest.");
                return true;
            }

            Inventory chest = loadChest(playerName);
            if(chest == null)
            {
                logger.info("Failed to load chest for " + playerName);
                player.sendMessage(ERROR + "Failed to load chest for " + playerName);
            }
            else
            {
                player.openInventory(chest);
            }
        }

        if(args.length == 1)
        {
            if(!sender.hasPermission("alphachest.admin"))
            {
                player.sendMessage(ERROR + "You don't have permission to use /chest on others.");
                return true;
            }

            Inventory chest = loadChest(args[0]);
            if(chest == null)
            {
                logger.info("Failed to load chest for " + args[0]);
                player.sendMessage(ERROR + "Failed to load chest for " + args[0]);
            }
            else
            {
                player.openInventory(chest);
            }
        }

        return true;
    }

    private boolean performClearChestCommand(CommandSender sender, String[] args)
    {
        if(args.length == 0 && (sender instanceof Player))
        {
            if(!sender.hasPermission("alphachest.chest"))
            {
                sender.sendMessage(ERROR + "You are not allowed to use this command.");
                return true;
            }

            removeChest(sender.getName().toLowerCase());
            sender.sendMessage(SUCCESS + "Successfully cleared your chest. It's not coming back.");
            return true;
        }

        if(args.length == 1)
        {
            if(!sender.hasPermission("alphachest.admin"))
            {
                sender.sendMessage(ERROR + "You are not allowed to clear another user's chests.");
                return true;
            }

            removeChest(args[0].toLowerCase());
            sender.sendMessage(SUCCESS + "Successfully cleared " + args[0] + "'s chest.");
            return true;
        }

        return false;
    }

    private Inventory loadChest(String playerName)
    {
        playerName = playerName.toLowerCase();
        File chestFile = new File(chestFolder, playerName + ".chest.yml");

        if(chestFile.exists())
        {
            try
            {
                return InventoryIO.loadFromYaml(chestFile, playerName);
            }
            catch(IOException | InvalidConfigurationException e)
            {
                logger.info("Failed to load chest for " + playerName);
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            return Bukkit.getServer().createInventory(null, 54, "AlphaChest - " + playerName);
        }
    }

    private void removeChest(String playerName)
    {
        playerName = playerName.toLowerCase();
        File chestFile = new File(chestFolder, playerName + ".chest.yml");

        if(chestFile.exists())
        {
            chestFile.delete();
        }
    }
}
