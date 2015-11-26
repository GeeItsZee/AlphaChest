package com.yahoo.tracebachi.AlphaChest.commands;

import com.yahoo.tracebachi.AlphaChest.VirtualChestManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ChestCommands implements CommandExecutor
{
    private static final String SUCCESS = ChatColor.translateAlternateColorCodes('&', "&8[&aAlphaChest&8]&a ");
    private static final String ERROR = ChatColor.translateAlternateColorCodes('&', "&8[&aAlphaChest&8]&c ");

    private final VirtualChestManager chestManager;

    public ChestCommands(VirtualChestManager chestManager)
    {
        this.chestManager = chestManager;
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
        else if(name.equalsIgnoreCase("savechests"))
        {
            return performSaveChestsCommand(sender);
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

            Inventory chest = chestManager.getChest(player.getName());
            if(chest == null)
            {
                chest = chestManager.createChest(player.getName());
            }

            player.openInventory(chest);
        }

        if(args.length == 1)
        {
            if(!sender.hasPermission("alphachest.admin"))
            {
                player.sendMessage(ERROR + "You don't have permission to use /chest on others.");
                return true;
            }

            Inventory chest = chestManager.getChest(args[0].toLowerCase());

            if(chest != null)
            {
                player.openInventory(chest);
            }
            else
            {
                player.sendMessage(ERROR + "That player does not have a chest.");
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

            chestManager.removeChest(sender.getName().toLowerCase());
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

            chestManager.removeChest(args[0].toLowerCase());
            sender.sendMessage(SUCCESS + "Successfully cleared " + args[0] + "'s chest.");
            return true;
        }

        return false;
    }

    private boolean performSaveChestsCommand(CommandSender sender)
    {
        if(sender.hasPermission("alphachest.save"))
        {
            chestManager.saveAll();
            sender.sendMessage(SUCCESS + "Saved all chests.");
            return true;
        }

        sender.sendMessage(ERROR + "You are not allowed to use this command.");
        return true;
    }
}
