package com.fCraft.PsP.commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.fCraft.PsP.PlayerData;
import com.fCraft.PsP.PsP;
import com.fCraft.PsP.commands.handler.Commands;
import com.fCraft.PsP.commands.util.CommandsUtility;
import com.fCraft.PsP.util.config.MessageConfig;
import com.fCraft.PsP.util.config.PsPConfiguration;

public class UserCommands extends CommandsUtility {
	
	/** Configuration Class object **/
	private static PsPConfiguration config = PsP.getPsPConfig();
	
	@Commands(name = "pinfo", syntax = "<user>", permission = "pinfo.basic.info", description = "Get info about yourself or another player")
	public void playerInfo(CommandSender sender, String[] args) {
		PlayerData pData = getTargetData(sender, args[0], true);

		// If the target player is not found in the map of available player info, return an error message
		if(pData == null)
			return;
		
		// Loop through the map of configuration values
		String result, key;
		for(Map.Entry<String, String> entry : config.getCfgMap().entrySet()) {
			key = entry.getKey();
			
			// If any key starts with 'output' we want the value because that is a line in our output message template
			if(key.startsWith("output")) {
				result = MessageConfig.getOutputLine(pData, entry.getValue());
				
				// After the output line gets processed and has the data for the target player put into it send the result
				if(result != null)
					sender.sendMessage(result);
			}
		}
	}
	
	@Commands(name = "pinfo", syntax = "<user> set placed <amount>", permission = "pinfo.admin.set", description = "Modify blocks placed info for a player")
	public void playerSetPlaced(CommandSender sender, String[] args) {
		String targetPlayer = args[0];
		String value = args[3];
		
		// Get the data for the target player if the player exists
		PlayerData pData = getTargetData(sender, targetPlayer, false);
		if(pData == null)
			return;
		
		// Check if the value the user wants to set can be parsed as a 'long'
		long placed;
		try {
			placed = Long.parseLong(value);
			
			if(placed >= 0) {
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "Replacing value " + 
									ChatColor.GREEN + pData.getPlaced() + 
									ChatColor.YELLOW + " with " + 
									ChatColor.GREEN + placed + 
									ChatColor.YELLOW + " for player " + pData.getDisplayName());
				pData.setPlaced(placed);
			} else {
				sender.sendMessage(ChatColor.RED + "Error: Value " + ChatColor.GOLD + value + ChatColor.RED + " is not greater than 0.");
				return;
			}
		} catch(NumberFormatException exc) {
			sender.sendMessage(ChatColor.RED + "Error: Invalid amount " + ChatColor.GOLD + value + ChatColor.RED + " could not be set.");
			return;
		}
	}

	@Commands(name = "pinfo", syntax = "<user> set broken <amount>", permission = "pinfo.admin.set", description = "Modify blocks broken info for a player")
	public void playerSetBroken(CommandSender sender, String[] args) {
		String targetPlayer = args[0];
		String value = args[3];
		
		PlayerData pData = getTargetData(sender, targetPlayer, false);
		if(pData == null)
			return;
		
		// Check if the value the user wants to set can be parsed as a 'long'
		long broken;
		try {
			broken = Long.parseLong(value);
			
			if(broken >= 0) {
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "Replacing value " + 
									ChatColor.RED + pData.getBroken() + 
									ChatColor.YELLOW + " with " + 
									ChatColor.RED + broken + 
									ChatColor.YELLOW + " for player " + pData.getDisplayName());
				pData.setBroken(broken);
			} else {
				sender.sendMessage(ChatColor.RED + "Error: Value " + ChatColor.GOLD + value + ChatColor.RED + " is not greater than 0.");
				return;
			}
		} catch(NumberFormatException exc) {
			sender.sendMessage(ChatColor.RED + "Error: Invalid amount " + ChatColor.GOLD + value + ChatColor.RED + " could not be set.");
			return;
		}
	}
	
	@Commands(name = "pinfo", syntax = "<user> set logins <amount>", permission = "pinfo.admin.set", description = "Modify login count info for a player")
	public void playerSetLogins(CommandSender sender, String[] args) {
		String targetPlayer = args[0];
		String value = args[3];
		
		PlayerData pData = getTargetData(sender, targetPlayer, false);
		if(pData == null)
			return;

		// Check if the value the user wants to set can be parsed as an 'int'
		int logins;
		try {
			logins = Integer.parseInt(value);
			
			if(logins >= 0) {
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "Replacing value " + 
									ChatColor.GREEN + pData.getLoginCount() + 
									ChatColor.YELLOW + " with " + 
									ChatColor.GREEN + logins + 
									ChatColor.YELLOW + " for player " + pData.getDisplayName());
				pData.setLoginCount(logins);
			} else {
				sender.sendMessage(ChatColor.RED + "Error: Value " + ChatColor.GOLD + value + ChatColor.RED + " is not greater than 0.");
				return;
			}
		} catch(NumberFormatException exc) {
			sender.sendMessage(ChatColor.RED + "Error: Invalid amount " + ChatColor.GOLD + value + ChatColor.RED + " could not be set.");
			return;
		}
	}
	
	@Commands(name = "pinfo", syntax = "<user> set played <amount>", permission = "pinfo.admin.set", description = "Modify time played info for a player")
	public void playerSetPlayed(CommandSender sender, String[] args) {
		String targetPlayer = args[0];
		String value = args[3];
		
		PlayerData pData = getTargetData(sender, targetPlayer, false);
		if(pData == null)
			return;

		// Check if the value the user wants to set can be parsed as a 'long'
		double played;
		try {
			played = Double.parseDouble(value);
			
			if(played >= 0) {
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "Replacing value " + 
									ChatColor.GREEN + pData.getMinutesPlayed() + 
									ChatColor.YELLOW + " with " + 
									ChatColor.GREEN + played + 
									ChatColor.YELLOW + " for player " + pData.getDisplayName());
				pData.setMinutesPlayed(played);
			} else {
				sender.sendMessage(ChatColor.RED + "Error: Value " + ChatColor.GOLD + value + ChatColor.RED + " is not greater than 0.0.");
				return;
			}
		} catch(NumberFormatException exc) {
			sender.sendMessage(ChatColor.RED + "Error: Invalid amount " + ChatColor.GOLD + value + ChatColor.RED + " could not be set.");
			return;
		}
	}

}
