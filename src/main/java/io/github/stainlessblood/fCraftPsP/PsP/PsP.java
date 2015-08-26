package io.github.stainlessblood.fCraftPsP.PsP;

import io.github.stainlessblood.fCraftPsP.listeners.BlockListener;
import io.github.stainlessblood.fCraftPsP.listeners.JoinListener;
import io.github.stainlessblood.fCraftPsP.listeners.QuitListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class PsP extends JavaPlugin {
	
	/** Hash Map of PlayerData objects for the players of the server **/
	private static ConcurrentHashMap<String, PlayerData> playerMap;
	
	/** Plugin's data folder **/
	private static File dFolder;
	
	/** Configuration Class object **/
	private Configuration config;
	
	@Override
	public void onEnable() {
		dFolder = getDataFolder();
		config = new Configuration();
		
		// Create the map and load with Online and Offline Players
		initMap();
		
		// Load the save file
		config.load();
		
		// Add the listeners
		getServer().getPluginManager().registerEvents(new JoinListener(), this);
		getServer().getPluginManager().registerEvents(new QuitListener(), this);
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
	}

	@Override
	public void onDisable() {
		config.save();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		
		String target;
		if(cmd.getName().equalsIgnoreCase("info")) {
			if(args.length < 1) {
				// Console cannot use /info, must use /info <player>
				if(!(sender instanceof Player)) {
					sender.sendMessage("Error: You cannot use /" + cmd.getName() + " to get info about the console.");
					return false;
				}
				
				// Player wants to get their so set the target to be the same player issuing the command
				player = (Player) sender;
				target = player.getName();
			} else {
				// Player want to get the info of another player
				target = args[0];
			}
			
			PlayerData pData;
			try {
				pData = playerMap.get(target);
				
				// If the target player is not found in the map of available player info return an error message
				if(pData == null) {
					sender.sendMessage(ChatColor.RED + "Error: Player " + target + " not found.");
					return false;
				}
			} catch(NullPointerException exc) {
				pData = new PlayerData();
			}

			// Send the information we have for the target player that we have to the command sender
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "Available info for " + ChatColor.WHITE + pData.getDisplayName());
			sender.sendMessage(ChatColor.YELLOW + addSeparator());
			sender.sendMessage(pData.getDisplayName() + ChatColor.YELLOW + " was last seen on - " + ChatColor.GOLD + pData.getSeen());
			sender.sendMessage(pData.getDisplayName() + ChatColor.YELLOW + " first logged in on " + ChatColor.GOLD + pData.getLogin());
			sender.sendMessage(ChatColor.YELLOW + "User has placed " + ChatColor.GREEN + pData.getPlaced() + ChatColor.YELLOW + " blocks, and destoryed " + ChatColor.RED + pData.getBroke() + ChatColor.YELLOW + " blocks.");
			sender.sendMessage(ChatColor.YELLOW + "User has logged in " + ChatColor.GREEN + pData.getLogCount() + ChatColor.YELLOW + " times since joining.");
			sender.sendMessage(ChatColor.YELLOW + "Last IP - " + ChatColor.RED + pData.getIp());
			
			// Get users with the same IP, check the map for matching IP's
			StringBuilder build = new StringBuilder();
			for(Entry<String, PlayerData> entry : playerMap.entrySet()) {
				if(entry.getValue().getIp().equalsIgnoreCase(pData.getIp()) && !entry.getValue().getName().equalsIgnoreCase(entry.getValue().getName())) {
					if(Bukkit.getOfflinePlayer(entry.getValue().getUuid()).isBanned())
						build.append(ChatColor.RED + entry.getValue().getName() + ", ");
					else
						build.append(entry.getValue().getDisplayName() + ", ");
				}
			}
			
			// Remove trailing comma separation from the matches or set the matches to 'None' if no matches found
			String matches;
			if(build.length() > 0)
				matches = build.substring(0, build.lastIndexOf(","));
			else
				matches = ChatColor.RED + "None";
			sender.sendMessage(ChatColor.YELLOW + "Other users with this IP - " + matches);
			return true;
		}
		
		// An error occurred
		sender.sendMessage(ChatColor.RED + "Error: Invalid command use.");
		return false;
	}

	/**
	 * Create the map that will hold the PlayerData objects and populate with any available player information
	 */
	private void initMap() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		playerMap = new ConcurrentHashMap<String, PlayerData>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			playerMap.put(p.getName(), new PlayerData(p.getName(),
											p.getDisplayName(), 
											p.getUniqueId(), 
											format.format(new Date(p.getLastPlayed())),
											p.getAddress().getHostString(), 
											format.format(new Date(p.getFirstPlayed())), 
											p.getStatistic(Statistic.LEAVE_GAME),
											0,
											0)
			);
		}

		for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			playerMap.put(p.getName(), new PlayerData(p.getName(),
											p.getName(), 
											p.getUniqueId(), 
											format.format(new Date(p.getLastPlayed())),
											"NAN", 
											format.format(new Date(p.getFirstPlayed())), 
											0,
											0,
											0)
			);
		}
	}

	/**
	 * A convenience method for a string of hyphens that is the length of the chat box minus 2 characters
	 * @return String - A string of '-' characters with length of 50 
	 */
	private String addSeparator() {
		String sep = "";
		
		for(int i = 0; i < 50; i++)
			sep += "-";
		return sep;
	}

	/** Getter to get a Map object of all the player's for the server that it knows about **/
	public static ConcurrentHashMap<String, PlayerData> getPlayerMap() {
		return playerMap;
	}

	/**
	 * Setter for the map containing the PlayerData information
	 * @param playerMap - The map of PlayerData to set
	 */
	public static void setPlayerMap(ConcurrentHashMap<String, PlayerData> playerMap) {
		PsP.playerMap = playerMap;
	}

	/** Getter for the plugin's data folder **/
	public static File getdFolder() {
		return dFolder;
	}

	/**
	 * Setter for the plugin's data folder
	 * @param dFolder - The file to set as the plugin's data folder
	 */
	public static void setdFolder(File dFolder) {
		PsP.dFolder = dFolder;
	}
}
