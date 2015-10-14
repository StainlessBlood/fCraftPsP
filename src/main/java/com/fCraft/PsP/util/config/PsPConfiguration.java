package com.fCraft.PsP.util.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.fCraft.PsP.PlayerData;
import com.fCraft.PsP.PsP;
import com.fCraft.PsP.util.Colors;

public class PsPConfiguration {
	
	/** Basic file configuration object to load, set, and get keys and values from the save file **/
	private FileConfiguration save;
	
	/** Basic file configuration object to load and get output message format values **/
	private FileConfiguration config;
	
	/** The file where the player data is saved **/
	private File saveFile;
	
	/** The file to load configuration values from**/
	private File cfgFile;
	
	/** PlayerData object used in the loading process of the save file **/
	private PlayerData pData;
	
	/** Key used in the mapping of info loaded from the files **/
	private String key;
	
	/** Map of strings to be output on use of info command **/
	LinkedHashMap<String, String> cfgMap;
	
	/**
	 * Default Constructor used on startup to create the YAML save file as well as
	 * the parent directory if neither exists
	 */
	public PsPConfiguration() {
		save = new YamlConfiguration();
		config = new YamlConfiguration();
		
		// Create a new YAML save file in the directory where the plugin's data files are located in
		saveFile = new File(PsP.getdFolder(), "psave.yml");
		cfgFile = new File(PsP.getdFolder(), "cfg.yml");
		
		// Create the file and the parent directory if they do not exist
		if(!saveFile.exists()) {
			saveFile.getParentFile().mkdirs();
			try {
				saveFile.createNewFile();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
		
		if(!cfgFile.exists())
			localFileCopy("/" + cfgFile.getName());
		cfgMap = new LinkedHashMap<String, String>();
	}

	/**
	 * Load the data from the YAML files
	 */
	public void load() {
		load(save, saveFile);
		load(config, cfgFile);
		
		processConfig();
	}
	
	/**
	 * Reads the data from the files and maps the info for later use
	 */
	private void load(FileConfiguration fileConfig, File file) {
		try {
			fileConfig.load(file);
			
			loadFile(fileConfig, null);
		} catch (FileNotFoundException exc) {
			exc.printStackTrace();
		} catch (IOException exc) {
			exc.printStackTrace();
		} catch (InvalidConfigurationException exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * The YAML files are loaded as mappings of key value pairs. So get the data we need
	 * and map the information to be used later on.
	 * @param fileConfig - The file configuration for the file we are currently processing
	 * @param set - The path of the information set in the file configuration
	 */
	private void loadFile(FileConfiguration fileConfig, String set) {
		boolean cfg = fileConfig.equals(config);
		boolean psave = fileConfig.equals(save);
		
		// Get the set of keys found on the current path
		Set<String> keys;
		if(set != null)
			keys = ((MemorySection) fileConfig.get(set)).getKeys(false);
		else
			keys = fileConfig.getKeys(false);

		// Loop through each one adding the pertinent data to the correct maps
		String path;
		for(String subSet : keys) {
			if(set != null)
				path = set + "." + subSet;
			else
				path = subSet;

			// If the current path is actually another section with multiple sub values, recursively call this method to get al lthe info
			if(fileConfig.get(path).getClass().equals(MemorySection.class)) {
				if(psave) {
					pData = new PlayerData();
					key = path;
				}
				
				loadFile(fileConfig, path);
			} else {
				Object val = fileConfig.get(path);
				
				// Set the fields for the PlayerData, else add the info to the map of config values
				if(psave) {
					pData.setDataField(subSet, val);
				} else if(cfg) {
					key = path;
					cfgMap.put(key, (String) val);
				}
			}
		}
		
		// Add the necessary info to the map of player info
		if(psave && set != null)
			PsP.getPlayerMap().put(key, pData);
		key = null;
	}

	/**
	 * Save the data in a YAML flat file
	 */
	public void save() {
		PlayerData val;
		String top;
		
		// For each player in the mapping of the all the players, get the key and value for each player
		for(Entry<String, PlayerData> entry : PsP.getPlayerMap().entrySet()) {
			top = entry.getKey();
			val = entry.getValue();

			// Set the key value pairings in the file configuration to those found in the map
			save.set(top, "");
			for(Field f : PlayerData.class.getDeclaredFields()) {
				String fieldName = f.getName();
				fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				
				// Get the data for each player by field name
				try {
					if(UUID.class.isAssignableFrom(f.getType()))
						save.set(top + "." + fieldName, ((UUID) f.get(val)).toString());
					else
						save.set(top + "." + fieldName, f.get(val));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Save the file
		try {
			save.save(saveFile);
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * If the config file doesn't yet exist make a copy of it from the packaged
	 * jar file and save the copy to the plugin data folder
	 * @param resource - The name of the file to copy
	 */
	private void localFileCopy(String resource) {
		InputStream in = null;
		OutputStream out = null;

		// Get the file from the jar
		try {
			in = this.getClass().getResourceAsStream(resource);
			out = new FileOutputStream(cfgFile.getPath());
			
			// Write it to the new file
			int length;
			byte[] buffer = new byte[1024];
			while ((length = in.read(buffer)) > 0)
				out.write(buffer, 0, length);
		} catch (FileNotFoundException exc) {
			exc.printStackTrace();
		} catch (IOException exc) {
			exc.printStackTrace();
		} finally {
			if(in != null)
				try {
					in.close();
				} catch (IOException exc) {
					exc.printStackTrace();
				}
			if(out != null)
				try {
					out.close();
				} catch (IOException exc) {
					exc.printStackTrace();
				}
		}
	}

	/**
	 * Process each line and replace keywords with the values associated to them
	 */
	private void processConfig() {
		String val;
		
		// Loop through all the output message lines
		for(Map.Entry<String, String> entry : cfgMap.entrySet()) {
			val = entry.getValue();
			
			// If any lines contain the '+' symbol check to see if its a color and replace with the associated ChatColor
			if(val.contains("+"))
				val = getChatColor(val);
			else
				continue;
			// Update the values in the map
			cfgMap.put(entry.getKey(), val);
		}
	}

	/**
	 * Given a String find any of the available colors in that string denoted by the color name surrounded by '+' characters
	 * @param str - The String to search for colors
	 * @return A new String with the colors replaced with their ChatColor values
	 */
	protected static String getChatColor(String str) {
		String result = str;
		
		// Check for each color in each String and replace with the ChatColor value
		for(Colors c : Colors.values()) {
			if(result.contains("+" + c + "+"))
				result = result.replaceAll("\\+" + c + "\\+", "" + c.getValue());
		}
		return result;
	}

	/** Getter to get a Map object of Strings output on use of the info command **/
	public LinkedHashMap<String, String> getCfgMap() {
		return cfgMap;
	}
	
	/**
	 * Setter for the map containing the command output strings
	 * @param cmdOutput - The map of strings to set it to
	 */
	public void setCfgMap(LinkedHashMap<String, String> cfgMap) {
		this.cfgMap = cfgMap;
	}
}
