package com.fCraft.PsP.listeners;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.fCraft.PsP.PlayerData;
import com.fCraft.PsP.PlayerLocation;
import com.fCraft.PsP.PsP;

public class QuitListener implements Listener {
	
	/** Map of online players, potential AFkers, for the server **/
	ConcurrentHashMap<Player, PlayerLocation> onlinePlayers = PsP.getOnlinePlayers();
	
	/** Map of players for the server **/
	ConcurrentHashMap<String, PlayerData> pMap = PsP.getPlayerMap();
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Get the player that quit
		Player pQuit = event.getPlayer();
		
		// Get the time of player's logout
		Date date = new Date();
		long time = date.getTime();
		
		// Player quit, no longer need to track if AFK
		onlinePlayers.remove(pQuit);
		
		// Get the date in Day-Month-Year format
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		// Get the current PlayerData of the person logging off and update a few values
		PlayerData pUpdate = pMap.get(pQuit.getName());
		pUpdate = PsP.updatePlayerRank(pUpdate);
		
		pUpdate.setLastSeen(format.format(now));
		pUpdate.setDisplayName(pQuit.getDisplayName());
		pUpdate.setLogoutTime(time);
		
		// Set the minutes played on server and subtract out any minutes spent afking
		double min = pUpdate.calculateTimePlayed(pUpdate.getLoginTime(), pUpdate.getLogoutTime());
		
		double minutes = pUpdate.getMinutesPlayed() + min - pUpdate.getViolations();
		pUpdate.setMinutesPlayed(new BigDecimal(minutes).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		
		// Update the value in the map for this player
		pMap.put(pQuit.getName(), pUpdate);
	}
	
}
