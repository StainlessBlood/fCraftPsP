package com.fCraft.PsP;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.fCraft.PsP.commands.UserCommands;
import com.fCraft.PsP.commands.UtilityCommands;
import com.fCraft.PsP.commands.handler.CommandsManager;
import com.fCraft.PsP.listeners.BlockListener;
import com.fCraft.PsP.listeners.JoinListener;
import com.fCraft.PsP.listeners.PlayerEventListener;
import com.fCraft.PsP.listeners.QuitListener;
import com.fCraft.PsP.util.config.PsPConfiguration;

public final class PsP extends JavaPlugin {
	
	/** Hash Map of Currently online players, used to track AFK players **/
	private static ConcurrentHashMap<Player, PlayerLocation> onlinePlayers;
	
	/** Hash Map of PlayerData objects for the players of the server **/
	private static ConcurrentHashMap<String, PlayerData> playerMap;

	/** Configuration Class object **/
	private static PsPConfiguration config;

	/** Instance of PsP plugin **/
	private static PsP instance;
	
	/** Commands Manager Class object **/
	private CommandsManager cmdManager;
	
	/** Plugin's data folder **/
	private static File dFolder;

	/** PeX rank-ladder **/
	private static String rankLadder;
	
	/**
	 * Constructor setting plugin instance
	 */
	public PsP() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		dFolder = getDataFolder();
		rankLadder = getLadder();
		
		// Initialize playerMap and load config and save files
		onPluginLoad();
		
		// Register and map the commands that are available to players
		cmdManager = new CommandsManager(this);
		cmdManager.registerCmds(new UserCommands());
		cmdManager.registerCmds(new UtilityCommands());

		// Add the listeners
		registerEvents();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new AFKTask(), 1200L, 1200L);
	}

	@Override
	public void onDisable() {
		// Do an update on ranks before the plugin gets disabled
		for(Map.Entry<String, PlayerData> entry : playerMap.entrySet()) {
			PlayerData pData = updatePlayerRank(entry.getValue());
			
			// Also do an update on display names
			Player p = Bukkit.getServer().getPlayer(pData.uuid);
			if(p != null)
				pData.setDisplayName(p.getDisplayName());
			
			playerMap.put(entry.getKey(), pData);
		}
		config.save();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String target;
		
		// First check the length of the arguments of the command the player provided
		if(args.length < 1) {
			// If player provided no arguments first check if the console issued the command
			if(!(sender instanceof Player)) {
				sender.sendMessage("Error: You cannot use /" + cmd.getName() + " to get info about the console.");
				return true;
			}
			Player player = (Player) sender;
			target = player.getName();
			
			// If no arguments were specified set it to the name of the Player issuing the command
			args = new String[]{target};
		}
		return cmdManager.executeCmds(sender, cmd, args);
	}


	/**
	 * Initialize the map that contains the players for the server and load
	 * the config file and the player save file that contains the information
	 * on all the players which then updates the map of players with this info.
	 */
	private void onPluginLoad() {
		playerMap = initMap();
		
		// Load config and save files
		config = new PsPConfiguration();
		config.load();
	}

	/**
	 * Do what you would if you were disabling the plugin, but then do
	 * what you would if you were loading the plugin on server startup.
	 */
	public void reload() {
		// Do an update on ranks before the plugin gets disabled
		for(Map.Entry<String, PlayerData> entry : playerMap.entrySet()) {
			PlayerData pData = updatePlayerRank(entry.getValue());
			
			// Also do an update on display names
			Player p = Bukkit.getServer().getPlayer(pData.uuid);
			if(p != null)
				pData.setDisplayName(p.getDisplayName());
			
			playerMap.put(entry.getKey(), pData);
		}
		config.save();
		
		// Re-Initialize the map of players and reload the config and save files
		onPluginLoad();
	}
	
	/**
	 * Using PeX, get the first rank-ladder group that is available.
	 * @return The group name
	 */
	private String getLadder() {
		String rankLadder = null;
		
		boolean pexRanks = false;
		if(Bukkit.getPluginManager().getPlugin("PermissionsEx") != null)
			pexRanks = true;
		
		if(pexRanks) {
			PermissionGroup group = PermissionsEx.getPermissionManager().getGroupList().get(0);
			if(group != null)
				rankLadder = group.getRankLadder();
		}

		return rankLadder;
	}

	/**
	 * Create the map that will hold the PlayerData objects and populate with any available player information
	 * @return 
	 */
	private ConcurrentHashMap<String, PlayerData> initMap() {
		ConcurrentHashMap<String, PlayerData> players = new ConcurrentHashMap<String, PlayerData>();
		onlinePlayers = new ConcurrentHashMap<Player, PlayerLocation>();
		
		// New PlayerData object
		PlayerData pData;
		
		// Loop through each player that is currently online and add them to the map
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		for(Player p : Bukkit.getOnlinePlayers()) {
			Location loc = p.getLocation();
			
			PlayerLocation pLoc = new PlayerLocation(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			onlinePlayers.put(p, pLoc);
			
			pData = new PlayerData(p.getUniqueId(), 
									p.getName(),
									p.getDisplayName(), 
									format.format(new Date(p.getLastPlayed())),
									format.format(new Date(p.getFirstPlayed())), 
									p.getAddress().getHostString(), 
									p.getStatistic(Statistic.LEAVE_GAME)
									);
			pData.setViolations(0);
			pData.setRankingInfoByDisplay();
			
			players.put(p.getName(), pData);
		}

		// Loop through each player that is available from the server save data and add them to the map
		for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			pData = new PlayerData(p.getUniqueId(), 
									p.getName(),
									p.getName(), 
									format.format(new Date(p.getLastPlayed())), 
									format.format(new Date(p.getFirstPlayed())), 
									"NAN", 
									0
									);
			players.put(p.getName(), pData);
		}
		return players;
	}

	/**
	 * Register the events to listen for while running
	 */
	private void registerEvents() {
		PluginManager pluginManager = Bukkit.getServer().getPluginManager();
		pluginManager.registerEvents(new JoinListener(), this);
		pluginManager.registerEvents(new QuitListener(), this);
		pluginManager.registerEvents(new BlockListener(), this);
		pluginManager.registerEvents(new PlayerEventListener(), this);
	}

	/**
	 * Get all players that match the target the user is looking for information on.
	 * @param target - The name of the player we want to find
	 * @return PlayerData object of the matching player from the map.
	 */
	public static ArrayList<PlayerData> getPlayerMatches(String target) {
		PlayerData pData = playerMap.get(target);

		// If we didn't find an exact match using the HashMap get, check for other kinds of matches
		ArrayList<PlayerData> matches;
		if(pData == null) {
			boolean displayMatch = false; 
			boolean	nameMatch = false;
			
			// First check to see if the target matches the Name or display name ignoring upper/lower case
			matches = new ArrayList<PlayerData>();
			for(Entry<String, PlayerData> entry : playerMap.entrySet()) {
				displayMatch = entry.getValue().getDisplayName().equalsIgnoreCase(target);
				nameMatch = entry.getKey().equalsIgnoreCase(target);
				
				// If we found an exact match return that one list match, otherwise check for partial matches
				if(nameMatch || displayMatch) {
					matches.add(entry.getValue());

					break;
				} else {
					boolean partialDisplay = entry.getValue().getDisplayName().toLowerCase().contains(target.toLowerCase());
					boolean partialName = entry.getKey().toLowerCase().contains(target.toLowerCase());
					
					// If the target didn't match any previous cases then check for names that contain the target and add them to a list
					if(partialDisplay || partialName)
						matches.add(entry.getValue());
				}
			}
		} else {
			// Found exact match to the target so set that to return
			matches = new ArrayList<PlayerData>();
			matches.add(pData);
		}
		return matches;
	}

	/**
	 * Update the rank info we have for a player. This is done when a player
	 * checks the info for a player, when a player leaves, or when the plugin
	 * gets disabled(when server stops/reloads).
	 * @param data - The PlayerData object of the player we want to update
	 * @return A new PlayerData object with the updated ranking information
	 */
	public static PlayerData updatePlayerRank(PlayerData data) {
		PlayerData pData = data;
		
		// Get the player by their UUID
		Player player = Bukkit.getServer().getPlayer(pData.uuid);
		if(player != null) {
			String rank = null;
			
			// If we have a valid rank ladder group get the name of the group the player falls under
			if(rankLadder != null) {
				rank = PermissionsEx.getUser(player).getRankLadderGroup(rankLadder).getName();

				// Check if their is a prefix that goes along with that group and get that too
				String prefix = PermissionsEx.getUser(player).getRankLadderGroup(rankLadder).getPrefix();
				if(rank != null && prefix != null)
					pData.setRankingInfo(prefix + rank);
			} else {
				pData.setRankingInfoByDisplay();
			}
		}
		return pData;
	}

	/** Used to get a Map object of player's currently online for tracking AFKers **/
	public static ConcurrentHashMap<Player, PlayerLocation> getOnlinePlayers() {
		return onlinePlayers;
	}

	/** Used to get a Map object of all the player's for the server that it knows about **/
	public static ConcurrentHashMap<String, PlayerData> getPlayerMap() {
		return playerMap;
	}

	/** Used to get configuration object **/
	public static PsPConfiguration getPsPConfig() {
		return config;
	}

	/** Used to get plugin instance **/
	public static PsP getPlugin() {
		return instance;
	}

	/** Used to get the plugin's data folder **/
	public static File getdFolder() {
		return dFolder;
	}

	/** Used to get the PeX rank-ladder for the server **/
	public static String getRankLadder() {
		return rankLadder;
	}
}
