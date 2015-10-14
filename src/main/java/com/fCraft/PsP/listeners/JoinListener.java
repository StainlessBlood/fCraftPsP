package com.fCraft.PsP.listeners;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fCraft.PsP.PlayerData;
import com.fCraft.PsP.PlayerLocation;
import com.fCraft.PsP.PsP;

public final class JoinListener implements Listener {
	
	/** Map of online players, potential AFkers, for the server **/
	ConcurrentHashMap<Player, PlayerLocation> onlinePlayers = PsP.getOnlinePlayers();
	
	/** Map of players for the server **/
	ConcurrentHashMap<String, PlayerData> pMap = PsP.getPlayerMap();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player pJoin = event.getPlayer();
		String pUUID = pJoin.getUniqueId().toString();

		int logins = pJoin.getStatistic(Statistic.LEAVE_GAME);
		logins = logins + 1;
		
		// Get the time of player's login
		Date date = new Date();
		long time = date.getTime();
		
		// Add player to currently online players to track for potential AFKing
		Location loc = pJoin.getLocation();
		
		PlayerLocation pLoc = new PlayerLocation(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		onlinePlayers.put(pJoin, pLoc);
		
		// This player exists in our records, update loginCount, IP, and UUID
		PlayerData pUpdate;
		if((pUpdate = pMap.get(pJoin.getName())) != null) { 
			pUpdate.updateOnLogin(logins, time);

			// Check if player IP is the same, if not update it
			if(!pUpdate.getIp().equalsIgnoreCase(pJoin.getAddress().getHostString()))
				pUpdate.setIp(pJoin.getAddress().getHostString());
			
			// Check if player UUID is the same, if not update it
			if(!pUpdate.getUuid().equals(pJoin.getUniqueId()))
				pUpdate.setUuid(pJoin.getUniqueId());
			pMap.put(pJoin.getName(), pUpdate);
			
			return;
		}
		
		// Check if player changed name, remove old mapping and put in the updated one with the new name
		JSONParser Parser = new JSONParser();
		JSONArray array = null;
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL("https://api.mojang.com/user/profiles/" + pUUID.replace("-", "") + "/names").openConnection();
			array = (JSONArray)Parser.parse(new InputStreamReader(connection.getInputStream()));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		Iterator<?> iterator = array.iterator();
		while (iterator.hasNext()) {
			JSONObject object = (JSONObject)iterator.next();
			String pName = (String)object.get("name");
			
			if((pUpdate = pMap.get(pName)) != null) {
				pUpdate.updateOnLogin(logins, time);
				
				pMap.remove(pName);
				pMap.put(pJoin.getName(), pUpdate);
				return;
			}
		}
		
		// Player is new to server, create new entry in the map
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		pUpdate = new PlayerData(pJoin.getUniqueId(), 
								pJoin.getName(), 
								pJoin.getDisplayName(), 
								format.format(new Date()),
								format.format(new Date(pJoin.getFirstPlayed())), 
								pJoin.getAddress().getHostString(), 
								pJoin.getStatistic(Statistic.LEAVE_GAME)
								);
		pUpdate.updateOnLogin(logins, time);
		
		pMap.put(pJoin.getName(), pUpdate);
	}
}
