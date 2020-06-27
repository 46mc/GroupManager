/**
 * 
 */
package org.anjocaido.groupmanager.commands;

import java.util.ArrayList;
import java.util.List;

import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author ElgarL
 *
 */
public class ManUCheckV extends BaseCommand implements TabCompleter {

	/**
	 * 
	 */
	public ManUCheckV() {}

	@Override
	protected boolean parseCommand(@NotNull String[] args) {

		// Validating state of sender
		if (dataHolder == null || permissionHandler == null) {
			if (!setDefaultWorldHandler(sender))
				return true;
		}
		// Validating arguments
		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED + "Review your arguments count! (/manucheckv <user> <variable>)");
			return true;
		}
		if ((plugin.isValidateOnlinePlayer()) && ((match = validatePlayer(args[0], sender)) == null)) {
			return false;
		}
		if (match != null) {
			auxUser = dataHolder.getUser(match.toString());
		} else {
			auxUser = dataHolder.getUser(args[0]);
		}
		// Validating permission
		auxGroup = auxUser.getGroup();
		auxGroup2 = permissionHandler.nextGroupWithVariable(auxGroup, args[1]);

		if (!auxUser.getVariables().hasVar(args[1])) {
			// Check sub groups
			if (!auxUser.isSubGroupsEmpty() && auxGroup2 == null)
				for (Group subGroup : auxUser.subGroupListCopy()) {
					auxGroup2 = permissionHandler.nextGroupWithVariable(subGroup, args[1]);
					if (auxGroup2 != null)
						continue;
				}
			if (auxGroup2 == null) {
				sender.sendMessage(ChatColor.YELLOW + "The user doesn't have access to that variable!");
				return true;
			}
		}
		// Seems OK
		if (auxUser.getVariables().hasVar(auxString)) {
			sender.sendMessage(ChatColor.YELLOW + "The value of variable '" + ChatColor.GOLD + args[1] + ChatColor.YELLOW + "' is: '" + ChatColor.GREEN + auxUser.getVariables().getVarObject(args[1]).toString() + ChatColor.WHITE + "'");
			sender.sendMessage(ChatColor.YELLOW + "This user own directly the variable");
		}
		sender.sendMessage(ChatColor.YELLOW + "The value of variable '" + ChatColor.GOLD + args[1] + ChatColor.YELLOW + "' is: '" + ChatColor.GREEN + auxGroup2.getVariables().getVarObject(args[1]).toString() + ChatColor.WHITE + "'");
		if (!auxGroup.equals(auxGroup2)) {
			sender.sendMessage(ChatColor.YELLOW + "And the value was inherited from group: " + ChatColor.GREEN + auxGroup2.getName());
		}

		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

		parseSender(sender, alias);
		
		List<String> result = new ArrayList<String>();
		
		/*
		 * Return a TabComplete for users.
		 */
		if (args.length == 1) {

			for (User user : dataHolder.getUserList()) {
				result.add(user.getLastName());
			}
			return result;
		}
		
		return null;
	}
}