package io.github.stainlessblood.fCraftPsP.PsP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration {
	
	/** Basic file configuration object to load, set, and get keys and values from the save file **/
	private FileConfiguration save;
	
	/** The flat file where the player data is saved **/
	private File saveFile;
	
	/**
	 * Default Constructor used on startup to create the YAML save file as well as
	 * the parent directory if neither exists
	 */
	public Configuration() {
		save = new YamlConfiguration();
		
		// Create a new YAML save file in the directory where the plugin's data files are located in
		saveFile = new File(PsP.getdFolder(), "psave.yml");
		
		// Create the file and the parent directory if they do not exist
		if(!saveFile.exists()) {
			saveFile.getParentFile().mkdirs();
			try {
				saveFile.createNewFile();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}
	
	/**
	 * Loads the data from the YAML flat file, if it exists
	 */
	public void load() {
		PlayerData pData;
		
		try {
			save.load(saveFile);
			
			// The file is loaded as a mapping of key value pairs, get the keys(Player names). E.g -> Steve = PlayerData
			Set<String> keys = save.getKeys(false);
			for(String set : keys) {
				Object val;
				
				// Create a new PlayerData object, this will happen for each player in the save file
				pData = new PlayerData();
				if(save.get(set).getClass().equals(MemorySection.class)) {
					// Get the name of the section head for the current Player in the save file, this is the Player's name
					val = ((MemorySection) save.get(set)).getName();
					
					// Get a set of key that are a subset of the previous key's value
					Set<String> subKeys = ((MemorySection) save.get(set)).getKeys(false);
					for(String subSet : subKeys) {
						Object subVal;
						
						// Set the path to value E.g -> Steve.UUID
						String path = set + "." + subSet;
						
						// Get the value at the specified path
						if(save.get(path).getClass().equals(String.class)) {
							subVal = save.getString(path);
						} else if(save.get(path).getClass().equals(Integer.class)) {
							subVal =  save.getInt(path);
						} else {
							subVal =  save.get(path);
						}
						// For the current player, set the data field specified by subSet to the value specified by subVal
						pData.setDataField(subSet, subVal);
					}
					
					// Add the player to a mapping of players as a key-value pair where the key is the player's name, val,
					// and the value is the PlayerData object, pData
					PsP.getPlayerMap().put((String) val, pData);
				} else {
					val =  save.get(set);
				}
			}
		} catch (FileNotFoundException exc) {
			exc.printStackTrace();
		} catch (IOException exc) {
			exc.printStackTrace();
		} catch (InvalidConfigurationException exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Save the data in a YAML flat file
	 */
	public void save() {
		PlayerData val;
		String top;
		
		// For each player in the mapping of the all the players get the key and value for each player
		for(Entry<String, PlayerData> entry : PsP.getPlayerMap().entrySet()) {
			top = entry.getKey();
			val = entry.getValue();
			
			// Set the key value pairings in the file configuration to those found in the map
			save.set(top, "");
			save.set(top + "." + "Name", val.getName());
			save.set(top + "." + "Display", val.getDisplayName());
			save.set(top + "." + "UUID", val.getUuid().toString());
			save.set(top + "." + "Seen", val.getSeen());
			save.set(top + "." + "First", val.getLogin());
			save.set(top + "." + "Count", val.getLogCount());
			save.set(top + "." + "Placed", val.getPlaced());
			save.set(top + "." + "Broke", val.getBroke());
			save.set(top + "." + "Ip", val.getIp());
		}
		
		// Save the file
		try {
			save.save(saveFile);
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}
}
