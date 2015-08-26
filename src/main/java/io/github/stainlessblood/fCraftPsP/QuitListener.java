package io.github.stainlessblood.fCraftPsP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
	
	/** Map of players for the server **/
	ConcurrentHashMap<String, PlayerData> pMap = PsP.getPlayerMap();
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Get the player that quit
		Player pQuit = event.getPlayer();
		
		// Get the date in Day-Month-Year format
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		// Duplicate player data object and change the last seen value
		PlayerData pUpdate = pMap.get(pQuit.getName());
		pUpdate.setSeen(format.format(now));
		pUpdate.setDisplayName(pQuit.getDisplayName());
		
		// Update the value in the map for this player
		pMap.put(pQuit.getName(), pUpdate);
	}
	
}
