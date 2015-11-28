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
