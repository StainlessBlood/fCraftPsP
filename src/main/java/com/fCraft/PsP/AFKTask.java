package com.fCraft.PsP;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AFKTask implements Runnable {

	/** Hash Map of Currently online players, used to track AFK players **/
	ConcurrentHashMap<Player, PlayerLocation> onlinePlayers = PsP.getOnlinePlayers();
	
	@Override
	public void run() {
		Location current, previous;
		
		int violation = 0;
		for(Map.Entry<Player, PlayerLocation> entry : onlinePlayers.entrySet()) {
			current = entry.getKey().getLocation();
			previous = entry.getValue();
			violation = getViolation(current, previous);

			// Update violation count for player
			PlayerData pData;
			PlayerLocation pLoc;
			if(violation == 0) {
				pLoc = new PlayerLocation(current.getWorld(), current.getX(), current.getY(), current.getZ());
				
				pData = PsP.getPlayerMatches(entry.getKey().getName()).get(0);
				pData.setViolations(pData.getViolations());
			} else {
				pLoc = entry.getValue();
				pData = PsP.getPlayerMatches(entry.getKey().getName()).get(0);
				
				if(!pLoc.isActive())
					pData.setViolations(pData.getViolations() + violation);
				else
					pLoc.setActive(false);
			}
			
			onlinePlayers.put(entry.getKey(), pLoc);
		}
	}

	/**
	 * Check if player has moved
	 * @param current - Current location data for the player
	 * @param previous - Last known location of player
	 * @return Returns 0 if player has moved 1 otherwise
	 */
	private int getViolation(Location current, Location previous) {
		if(current.getWorld() != previous.getWorld())
			return 0;
		else if(current.getX() != previous.getX())
			return 0;
		else if(current.getY() != previous.getY())
			return 0;
		else if(current.getZ() != previous.getZ())
			return 0;
		return 1;
	}

}
