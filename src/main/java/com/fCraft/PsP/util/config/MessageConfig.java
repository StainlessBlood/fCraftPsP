package com.fCraft.PsP.util.config;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.fCraft.PsP.PlayerData;
import com.fCraft.PsP.PsP;

public class MessageConfig {
	
	/** Available keywords **/
	protected static String[] keywords = {
		"BLANK", 
		"BROKEN", 
		"CURRENTRANK", 
		"DISPLAYCOLOR", 
		"DISPLAYNAME", 
		"FIRSTSEEN", 
		"IPADDRESS", 
		"LASTSEEN", 
		"LOGINCOUNT", 
		"MATCHES", 
		"MINUTES", 
		"NAME", 
		"PLACED", 
		"RANKEDON", 
		"SEPARATOR", 
		"UNUSED"
	};
	
	/**
	 * When a players uses the info command the resulting output uses our output template
	 * loaded from the cfg.yml file. This method checks the current line from the template
	 * for matching keywords and replaces any of the matching keywords with actual data 
	 * from our player info.
	 * @param pData - The player data we are using to replace matching keywords with.
	 * @param str - The current line to check for matching keywords.
	 * @return String with all matching keywords replaced with target player information.
	 */
	public static String getOutputLine(PlayerData pData, String str) {
		String result = str;
		
		for(String key : keywords) {
			if(result.contains(key)) {
				if(key.equals("UNUSED"))
					result = null;
				else if(key.equals("SEPARATOR"))
					result = result.replaceAll(key, addSeparator());
				else if(key.equals("MATCHES"))
					result = result.replaceAll(key, getMatchingPlayerIPs(pData));
				else if(key.equals("RANKEDON"))
					result = result.replaceAll(key, formatRankedDate(pData.getRankInfo()[1]));
				else if(key.equals("PLACED"))
					result = result.replaceAll(key, formatBlocksModified(pData.getPlaced()));
				else if(key.equals("BROKEN"))
					result = result.replaceAll(key, formatBlocksModified(pData.getBroken()));
				else if(key.equals("DISPLAYCOLOR")) {
					String data = pData.getDataByKey(key);
					
					if(data != null)
						result = result.replaceAll("\\+" + key + "\\+", data);
					else
						result = result.replaceAll("\\+" + key + "\\+", "" + ChatColor.WHITE);
				} else if(key.equals("CURRENTRANK")) {
					String data = pData.getDataByKey(key);
					
					if(data != null)
						result = result.replaceAll(key, data);
					else
						result = result.replaceAll(key, ChatColor.WHITE + "Non-Ranked");
				} else {
					String data = pData.getDataByKey(key);
					
					if(data != null)
						result = result.replaceAll(key, data);
					else
						result = result.replaceAll(key, ChatColor.RED + "NAN");
				}
			}
		}
		return result;
	}

	/**
	 * Check the map of players for any IPs that match the target player. If we find
	 * another player in our map with a matching IP append the Name of the player to
	 * our list and continue searching for any other possible matches.
	 * @param pData - The current player user is seeking information on
	 * @return String of player names for Players who shared the same IP as the target Player
	 */
	private static String getMatchingPlayerIPs(PlayerData pData) {
		ArrayList<String> build = new ArrayList<String>();
		
		// Check the map of players for any matching IPs
		ConcurrentHashMap<String, PlayerData> map = PsP.getPlayerMap();
		for(Entry<String, PlayerData> entry : map.entrySet()) {
			PlayerData pVal = entry.getValue();

			if(pVal.getIp().equalsIgnoreCase(pData.getIp()) && !pVal.getName().equalsIgnoreCase(pData.getName())) {
				if(Bukkit.getOfflinePlayer(pVal.getUuid()).isBanned())
					build.add(ChatColor.RED + pVal.getName());
				else
					build.add(pVal.getDisplayName());
			}
		}
		
		// Join all the matches into one string with a comma separator between each name
		String matches;
		if(build.size() > 0)
			matches = StringUtils.join(build, ", ");
		else
			matches = ChatColor.RED + "None";
		return matches;
	}
	
	/**
	 * Used to get the time difference between the current day and the day a player was
	 * ranked on in a nice format. Given the dates, get the difference in days and format
	 * it in terms of days, weeks, months, or even years.
	 * @param date - The current player user is seeking information on
	 * @return A new string representing the time between today and the day a player was
	 * ranked on
	 */
	private static String formatRankedDate(Object date) {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date today = new Date();
		
		if(date == null)
			date = format.format(today);
		
		// Format the length of time since the player's ranked changed
		String result = null;
		try {
			Date d = format.parse((String) date);
			
			// Get the number of days between today and the day player's rank changed
			long diff = today.getTime() - d.getTime();
			long days = TimeUnit.MILLISECONDS.toDays(diff);

			// Format it for nicer look
			if(days > 365)
				result = (int)(days / 365.25) + " year";
			else if(days > 30)
				result = (int)(days / 30.44) + " month";
			else if(days > 7)
				result = (int)(days / 7) + " week";
			else
				result = days + " day";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// If we don't have a result just set it at 0
		if(result == null)
			result = "0 days";
		else {
			int num = Integer.parseInt(result.substring(0, 1));
			
			// Check if the number at the start of our result so we can add an 's' to the end of result for such cases
			if(num > 1 || num == 0)
				result = result.concat("s");
		}
		return result;
	}

	/**
	 * Used to shorten up the blocks a player has placed or broken so as
	 * not to have unnecessarily large numbers in the info a player sees.
	 * @param blocksModified - The number of blocks placed or broken to format
	 * @return A string with a shorthand format on blocks placed/broken amount
	 */
	private static String formatBlocksModified(long blocksModified) {
		String result;
		
		// Format it to look nicer, e.g. -> 567843 prints as 567K
		if(blocksModified >= Math.pow(10, 15))
			result = new BigDecimal(blocksModified / Math.pow(10d, 15)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "Q";
		else if(blocksModified >= Math.pow(10, 12))
			result = new BigDecimal(blocksModified / Math.pow(10d, 12)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "T";
		else if(blocksModified >= Math.pow(10, 9))
			result = new BigDecimal(blocksModified / Math.pow(10d, 9)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "B";
		else if(blocksModified >= Math.pow(10, 6))
			result = new BigDecimal(blocksModified / Math.pow(10d, 6)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "M";
		else if(blocksModified >= Math.pow(10, 5))
			result = new BigDecimal(blocksModified / Math.pow(10d, 3)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "K";
		else
			result = blocksModified + "";
		return result;
	}

	/**
	 * A convenience method for a string of hyphens that is the length of the chat box minus about 2 characters
	 * @return A string of separator characters with length of 50 
	 */
	private static String addSeparator() {
		String full = "";
		String sepChar = PsP.getPsPConfig().getCfgMap().get("separator");
		
		// If the separator key is missing from the config file then use this default value
		if(sepChar == null)
			sepChar = "-";
		
		// Create a string of separator characters approximately the length of the chat box
		for(int i = 0; i < 50; i++)
			full += sepChar;
		return full;
	}

}
