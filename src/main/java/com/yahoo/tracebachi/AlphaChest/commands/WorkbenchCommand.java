package com.yahoo.tracebachi.AlphaChest.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements CommandExecutor
{
    private static final String ERROR = ChatColor.translateAlternateColorCodes('&', "&8[&aAlphaChest&8]&c ");

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ERROR + "Command can only be performed by a player.");
            return true;
        }

        Player player = (Player) sender;
        if(player.hasPermission("alphachest.workbench"))
        {
            player.openWorkbench(null, true);
        }
        else
        {
            player.sendMessage(ERROR + "You're not allowed to use this command.");
        }

        return true;
    }
}
