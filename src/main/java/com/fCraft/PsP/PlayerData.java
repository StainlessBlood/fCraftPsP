package com.fCraft.PsP;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerData extends PlayerAFK {

	/** Player's UUID **/
	public UUID uuid;
	
	/** Player's Username **/
	public String name;
	
	/** Player's Display Name (If server has ranks/player has nickname) **/
	public String displayName;
	
	/** Player's current rank and date they got that rank **/
	public Object[] rankInfo;
	
	/** Player was last seen at date **/
	public String lastSeen;

	/** Player's first login date **/
	public String firstSeen;

	/** Player's last know IP address **/
	public String ip;
	
	/** Number of minutes the player has played on the server **/
	public double minutesPlayed;
	
	/** Player's total number of logins **/
	public int loginCount;
	
	/** Player's number of blocks placed **/
	public long placed;
	
	/** Player's number of blocks broken **/
	public long broken;

	/**
	 * Default Constructor for empty player data objects
	 */
	public PlayerData() {
		this.rankInfo = new String[2];
		this.uuid = null;
		this.name = null;
		this.displayName = null;
		this.lastSeen = null;
		this.firstSeen = null;
		this.ip = null;
		this.loginCount = 0;
		this.placed = 0L;
		this.broken = 0L;
		this.minutesPlayed = 0.0;
		
		this.violations = 0;
		this.loginTime = 0;
		this.logoutTime = 0;
	}
	
	/** 
	 * This constructor is used to set up player data objects to hold the below parameters which are mapped and accessed later on.
	 * @param uuid - Player's UUID
	 * @param name - Player's Username
	 * @param displayName - Player's DisplayName, as server might have ranks or player might have a nickname set
	 * @param lastSeen - Player's last seen on date
	 * @param firstSeen - Player's first login date
	 * @param ip - Player's last known IP
	 * @param logCount - Player's total number of logins
	 */
	public PlayerData(UUID uuid, String name, String displayName, String lastSeen, String firstSeen, String ip, int loginCount) {
		this.rankInfo = new String[2];
		this.uuid = uuid;
		this.name = name;
		this.displayName = displayName;
		this.lastSeen = lastSeen;
		this.firstSeen = firstSeen;
		this.ip = ip;
		this.loginCount = loginCount;
		this.placed = 0L;
		this.broken = 0L;
		this.minutesPlayed = 0;
		
		this.violations = 0;
		this.loginTime = 0;
		this.logoutTime = 0;
	}

	/**
	 * Get player data using specific keywords. When a player issues the info command,
	 * the resulting output comes from the template found in the file cfg.yml and uses
	 * this method to find what player data to send based off the keywords.
	 * @param keyword - The String used to match against actual player data.
	 * @return String of player data that pertains to the matching keyword.
	 */
	public String getDataByKey(String keyword) {
		String data;
		
		if(keyword.equalsIgnoreCase("BLANK"))
			data = "";
		else if(keyword.equalsIgnoreCase("BROKEN"))
			data = "" + this.getBroken();
		else if(keyword.equalsIgnoreCase("CURRENTRANK"))
			data = (String) this.getRankInfo()[0];
		else if(keyword.equalsIgnoreCase("DISPLAYCOLOR")) {
			ChatColor color = ChatColor.getByChar(this.getDisplayName().charAt(1));
			
			if(color != null)
				data = "" + color;
			else
				data = null;
		} else if(keyword.equalsIgnoreCase("DISPLAYNAME"))
			data = this.getDisplayName();
		else if(keyword.equalsIgnoreCase("FIRSTSEEN"))
			data = this.getFirstSeen();
		else if(keyword.equalsIgnoreCase("IPADDRESS"))
			data = this.getIp();
		else if(keyword.equalsIgnoreCase("LASTSEEN"))
			data = this.getLastSeen();
		else if(keyword.equalsIgnoreCase("LOGINCOUNT"))
			data = "" + this.getLoginCount();
		else if(keyword.equalsIgnoreCase("MINUTES"))
			data = "" + this.getMinutesPlayed();
		else if(keyword.equalsIgnoreCase("NAME"))
			data = this.getName();
		else if(keyword.equalsIgnoreCase("PLACED"))
			data = "" + this.getPlaced();
		else if(keyword.equalsIgnoreCase("RANKEDON"))
			data = (String) this.getRankInfo()[1];
		else
			return null;
		return data;
	}
	
	/**
	 * Used when loading the flat file for the players saved info. Each Player's info is read,
	 * saved in a player data object, and then mapped for use later on. The flat file is saved in
	 * key value pairs.
	 * @param key - What we use to get and set values for the player's info
	 * @param value - The value for for that key
	 * @return True - if the key is valid and the object was set.
	 */
	public boolean setDataField(String key, Object value) {
		String fieldName;
		
		// Loop through all the available fields the PlayerData class has
		for(Field f : this.getClass().getDeclaredFields()) {
			fieldName = f.getName();
			
			// If we find a matching field we want to set it to some value
			if(fieldName.equalsIgnoreCase(key)) {
				Class<?> c = f.getType();
				
				// Set the field to the value we have, making sure types match
				try {
					if(String.class.isAssignableFrom(c))
						f.set(PlayerData.this, value);
					else if(Object[].class.isAssignableFrom(c))
						f.set(PlayerData.this, ((ArrayList<?>)value).toArray());
					else if(UUID.class.isAssignableFrom(c))
						f.set(PlayerData.this, UUID.fromString((String) value));
					else if(double.class.isAssignableFrom(c))
						f.set(PlayerData.this, (double) value);
					else if(int.class.isAssignableFrom(c))
						f.set(PlayerData.this, (int) value);
					else if(long.class.isAssignableFrom(c))
						f.set(PlayerData.this, value);
				} catch (IllegalArgumentException exc) {
					exc.printStackTrace();
				} catch (IllegalAccessException exc) {
					exc.printStackTrace();
				} catch (SecurityException exc) {
					exc.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * A set of values we want to update/set/check when a player logs on
	 * @param logins - The number of logins for the player logging in
	 * @param time - The time at which a player joins the server
	 */
	public void updateOnLogin(int logins, long time) {
		this.setViolations(0);
		this.setLoginCount(logins);
		this.setLoginTime(time);
		
		this.setRankingInfoByDisplay();
	}

	/**
	 * Update the display name so that their isn't discrepancies between what we have
	 * for a rank and what we see the display name as.
	 * @param player - The player who's name we are updating
	 */
	public void updateDisplayName(Player player) {
		String result = null;
		
		// Get the PeX rank-ladder
		String ladder = PsP.getRankLadder();
		if(ladder != null) {
			String prefix = PermissionsEx.getUser(player).getRankLadderGroup(ladder).getPrefix();
			prefix = ampToSec(prefix);
			
			// Get the prefix at the beginning of the player's name and check against group prefixes
			String def = null;
			for(PermissionGroup group : PermissionsEx.getPermissionManager().getGroupList()) {
				String groupPrefix = group.getPrefix();
				groupPrefix = ampToSec(groupPrefix);

				// Check and set default group
				if(group.getOption("default").equalsIgnoreCase("true"))
					def = groupPrefix + this.getDisplayName();

				// If there is a match update to new prefix
				if(this.getDisplayName().startsWith(groupPrefix))
					result = this.getDisplayName().replace(groupPrefix, prefix);
			}
			
			// If we found a valid rank this player falls under set the display name to reflect that else use default rank
			if(result != null)
				this.setDisplayName(result);
			else
				if(def != null)
					this.setDisplayName(def);
				else
					this.setDisplayName(this.getDisplayName());
		}
	}

	/**
	 * Set the rank for the player by checking the formatting for the user's 
	 * display name against rank formatting found in the map of configuration 
	 * values. If player's display name is '§7Notch' and we have a rank value
	 * of '§7' in the configuration value map under ranks, get the rank
	 * associated to that value. Also set the date this as today for when the
	 * player got this rank.
	 */
	public void setRankingInfoByDisplay() {
		String result = null;
		
		String def = "";
		if(PsP.getRankLadder() != null) {
			// Loop through all available PeX groups defined in PeX permissions file
			for(PermissionGroup group : PermissionsEx.getPermissionManager().getGroupList()) {
				String groupPrefix = group.getPrefix();
				groupPrefix = ampToSec(groupPrefix);

				// Check and set default group
				if(group.getOption("default").equalsIgnoreCase("true"))
					def = groupPrefix + group.getName();
				
				// If player display name starts with same prefix as the group then set the result to 'prefix + rank'
				if(this.getDisplayName().startsWith(groupPrefix))
					result = groupPrefix + group.getName();
			}
		}
		
		// If we couldn't find a rank group for the player use the default group value
		if(result == null)
			result = def;
		setRankingInfo(result);
	}

	/**
	 * Set the rank information for a player. If no current rank information for a player
	 * is available use the value given by the 'str' parameter for the rank and today's
	 * date for the date ranked on portion.
	 * @param str - The rank to set for the player
	 */
	public void setRankingInfo(String str) {
		String result = ampToSec(str);
		Object[] rank;
		
		// If this player doesn't already have information set for their rank use the value of result parameter
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if(this.getRankInfo()[0] == null || this.getRankInfo()[1] == null) {
			rank = new String[2];
			rank[0] = result;
			rank[1] = format.format(new Date());
		} else {
			rank = this.getRankInfo();

			// Update the rank value and the date this rank was changed
			if(!rank[0].toString().equalsIgnoreCase(result)) {
				rank[0] = result;
				rank[1] = format.format(new Date());
			}
		}
		this.setRankInfo(rank);
	}

	/**
	 * Replace all occurrences of & with §
	 * @param str - The string to perform the replace on
	 * @return A new string with all ampersands replaced by section symbols
	 */
	private String ampToSec(String str) {
		String result = str;
		
		// Perform as long as not a null string
		if(result != null)
			if(result.contains("&"))
				result = result.replaceAll("&", "§");
		return result;
	}

	/** Used to get the player's UUID **/
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Used to set the player's UUID
	 * @param uuid - The UUID to set for the player
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/** Used to get the player's name **/
	public String getName() {
		return name;
	}
	
	/**
	 * Used to set the player's name
	 * @param name - The name to set for the player
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** Used to get the player's display name **/
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Used to set the player's display name
	 * @param displayName - The display name to set for the player
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/** Used to get the player's ranking info(Current rank and date ranked on) **/
	public Object[] getRankInfo() {
		return rankInfo;
	}

	/**
	 * Used to set the player's ranking info
	 * @param rankInfo - Array to set the current rank and date ranked on for the player
	 */
	public void setRankInfo(Object[] rankInfo) {
		this.rankInfo = rankInfo;
	}

	/** Used to get the player's last seen date **/
	public String getLastSeen() {
		return lastSeen;
	}

	/**
	 * Used to set the player's last seen date
	 * @param lastSeen - The last seen date for the player, string format
	 */
	public void setLastSeen(String lastSeen) {
		this.lastSeen = lastSeen;
	}

	/** Used to get the player's first login date **/
	public String getFirstSeen() {
		return firstSeen;
	}

	/**
	 * Used for the player's first login date
	 * @param firstSeen - The first logged in date to set for the player, string format
	 */
	public void setFirstSeen(String firstSeen) {
		this.firstSeen = firstSeen;
	}

	/** Used to get the player's IP **/
	public String getIp() {
		return ip;
	}

	/**
	 * Used for the player's last known IP
	 * @param ip - The last known IP to set for the player, string format
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/** Used to get the player's total number of minutes played on the server **/
	public double getMinutesPlayed() {
		return new BigDecimal(minutesPlayed).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * Used to set how long the player has played on the server in minutes
	 * @param minutesPlayed - The number of minutes to set to
	 */
	public void setMinutesPlayed(double minutesPlayed) {
		this.minutesPlayed = minutesPlayed;
	}

	/** Used to get the player's total number of logins **/
	public int getLoginCount() {
		return loginCount;
	}

	/**
	 * Used for the player's total number of logins
	 * @param logCount - The total number of logins to set for the player
	 */
	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}

	/** Used to get the player's total blocks placed amount **/
	public long getPlaced() {
		return placed;
	}

	/**
	 * Used for the player's total number of blocks placed
	 * @param placed - The total number of blocks placed to set for the player
	 */
	public void setPlaced(long placed) {
		this.placed = placed;
	}

	/** Used to get the player's total blocks broken amount **/
	public long getBroken() {
		return broken;
	}

	/**
	 * Used for the player's total number of blocks broken
	 * @param broke - The total number of blocks broken to set for the player
	 */
	public void setBroken(long broken) {
		this.broken = broken;
	}
}
