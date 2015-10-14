package com.fCraft.PsP.commands.util;

import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fCraft.PsP.PlayerData;
import com.fCraft.PsP.PsP;
import com.fCraft.PsP.commands.handler.CommandsListener;
import com.fCraft.PsP.commands.handler.CommandsManager;

public abstract class CommandsUtility implements CommandsListener  {
	
	/** Instance of the commands manager **/
	protected CommandsManager manager;
	
	@Override
	public void onRegister(CommandsManager manager) {
		this.manager = manager;
	}
	
	/**
	 * Used to get the PlayerData object for the target player. If we can't find a match
	 * inform the player the target player wasn't found. If multiple matches are found 
	 * send the matches to the command sender. Depending on which command is being used
	 * go through the matches we find for the target player and update the display names.
	 * @param sender - The player using the command
	 * @param targetPlayer - The target player the command user is interested in
	 * @param update - If a player is using any command other than the one to get info on 
	 * themselves or another player then this should be false. True otherwise because we
	 * want to update the display names of the matches we find for the target player
	 * @return A PlayerData object if we found a single match to our target player or null
	 * if no match/matches were found
	 */
	protected PlayerData getTargetData(CommandSender sender, String targetPlayer, boolean update) {
		PlayerData pData;
		
		try {
			// Get the target's PlayerData and if we are looking up info update the display names
			ArrayList<PlayerData> matches = PsP.getPlayerMatches(targetPlayer);
			if(update) {
				for(int i = 0; i < matches.size(); i++) {
					PlayerData current = PsP.updatePlayerRank(matches.get(i));
					
					// Also do an update on display names
					Player p = Bukkit.getServer().getPlayer(current.uuid);
					if(p != null)
						current.updateDisplayName(p);
					matches.set(i, current);
				}
			}
			
			// If no match was found let player know and return, otherwise return/send resulting info to player
			if(matches.size() == 0) {
				pData = null;
			} else if(matches.size() == 1) {
				pData = matches.get(0);
			} else {
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "Matching Players: ");
	
				// Check to see if any matches are banned players
				Set<OfflinePlayer> banned = Bukkit.getBannedPlayers();
				ArrayList<String> names = new ArrayList<String>();
				for(int i = 0; i < matches.size(); i++) {
					OfflinePlayer p = Bukkit.getOfflinePlayer(matches.get(i).getUuid());
					
					// If player is banned, italicize the name and add a red asterisk after it
					if(banned.contains(p))
						names.add(ChatColor.ITALIC + matches.get(i).getDisplayName() + ChatColor.RED + "*" + ChatColor.RESET);
					else
						names.add(matches.get(i).getDisplayName());
				}
				sender.sendMessage(StringUtils.join(names.toArray(), ", "));
				return null;
			}
		} catch(NullPointerException exc) {
			pData = null;
		}
		
		if(pData == null)
			sender.sendMessage(ChatColor.RED + "Error: Player " + ChatColor.GOLD + targetPlayer + ChatColor.RED + " not found.");
		return pData;
	}

}
