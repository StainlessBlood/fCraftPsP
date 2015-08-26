package io.github.stainlessblood.fCraftPsP;

import java.util.UUID;

public class PlayerData {

	/** Player's Username **/
	private String name;
	
	/** Player's Display Name (If server has ranks/player has nickname) **/
	private String displayName;
	
	/** Player's UUID **/
	private UUID uuid;
	
	/** Player was last seen at date **/
	private String seen;
	
	/** Player's last know IP address **/
	private String ip;
	
	/** Player's first login date **/
	private String login;
	
	/** Player's total number of logins **/
	private int logCount;
	
	/** Player's number of blocks placed **/
	private int placed;
	
	/** Player's number of blocks broken **/
	private int broke;
	
	// Default Constructor for empty player data objects
	public PlayerData() {
		this.name = null;
		this.displayName = null;
		this.uuid = null;
		this.seen = null;
		this.ip = null;
		this.login = null;
		this.logCount = 0;
		this.placed = 0;
		this.broke = 0;
	}
	
	/** 
	 * This constructor is used to set up player data objects to hold the below parameters which are mapped and accessed later on.
	 * @param name - Player's Username
	 * @param displayName - Player's DisplayName, as server might have ranks or player might have a nickname set
	 * @param uuid - Player's UUID
	 * @param seen - Player's last seen on date
	 * @param ip - Player's last known IP
	 * @param login - Player's first login date
	 * @param logCount - Player's total number of logins
	 * @param placed - Player's total number of blocks placed
	 * @param broke - Player's total number of blocks broken
	 */
	public PlayerData(String name, String displayName, UUID uuid, String seen, String ip, String login, int logCount, int placed, int broke) {
		this.name = name;
		this.displayName = displayName;
		this.uuid = uuid;
		this.seen = seen;
		this.ip = ip;
		this.login = login;
		this.logCount = logCount;
		this.placed = placed;
		this.broke = broke;
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
		if(key.equalsIgnoreCase("name"))
			this.setName((String) value);
		else if(key.equalsIgnoreCase("display"))
			this.setDisplayName((String) value);
		else if(key.equalsIgnoreCase("uuid"))
			this.setUuid(UUID.fromString((String) value));
		else if(key.equalsIgnoreCase("seen"))
			this.setSeen((String) value);
		else if(key.equalsIgnoreCase("first"))
			this.setLogin((String) value);
		else if(key.equalsIgnoreCase("ip"))
			this.setIp((String) value);
		else if(key.equalsIgnoreCase("count"))
			this.setLogCount((int) value);
		else if(key.equalsIgnoreCase("placed"))
			this.setPlaced((int) value);
		else if(key.equalsIgnoreCase("broke"))
			this.setBroke((int) value);
		else
			return false;
		return true;
	}

	/** Getter to get the player's name **/
	public String getName() {
		return name;
	}
	
	/**
	 * Setter to set the player's name
	 * @param name - The name to set for the player
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** Getter to get the player's display name **/
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Setter to set the player's display name
	 * @param displayName - The display name to set for the player
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/** Getter to get the player's UUID **/
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Setter to set the player's UUID
	 * @param uuid - The UUID to set for the player
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/** Getter to get the player's last seen date **/
	public String getSeen() {
		return seen;
	}

	/**
	 * Setter to set the player's last seen date
	 * @param seen - The last seen date for the player, string format
	 */
	public void setSeen(String seen) {
		this.seen = seen;
	}

	/** Getter to get the player's IP **/
	public String getIp() {
		return ip;
	}

	/**
	 * Setter for the player's last known IP
	 * @param ip - The last known IP to set for the player, string format
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/** Getter to get the player's first login date **/
	public String getLogin() {
		return login;
	}

	/**
	 * Setter for the player's first login date
	 * @param login - The first logged in date to set for the player, string format
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/** Getter to get the player's total number of logins **/
	public int getLogCount() {
		return logCount;
	}

	/**
	 * Setter for the player's total number of logins
	 * @param logCount - The total number of logins to set for the player
	 */
	public void setLogCount(int logCount) {
		this.logCount = logCount;
	}

	/** Getter to get the player's total blocks placed amount **/
	public int getPlaced() {
		return placed;
	}

	/**
	 * Setter for the player's total number of blocks placed
	 * @param placed - The total number of blocks placed to set for the player
	 */
	public void setPlaced(int placed) {
		this.placed = placed;
	}

	/** Getter to get the player's total blocks broken amount **/
	public int getBroke() {
		return broke;
	}

	/**
	 * Setter for the player's total number of blocks broken
	 * @param broke - The total number of blocks broken to set for the player
	 */
	public void setBroke(int broke) {
		this.broke = broke;
	}
}
