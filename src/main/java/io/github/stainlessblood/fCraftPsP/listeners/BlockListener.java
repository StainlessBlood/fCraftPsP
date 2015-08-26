package io.github.stainlessblood.fCraftPsP.listeners;

import io.github.stainlessblood.fCraftPsP.PsP.PlayerData;
import io.github.stainlessblood.fCraftPsP.PsP.PsP;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
	
	/** Map of players for the server **/
	ConcurrentHashMap<String, PlayerData> pMap = PsP.getPlayerMap();
	
	/** Player data object **/
	PlayerData pUpdate;
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent evt) {
		// Get the player that placed a block
		Player placer = evt.getPlayer();
		
		// Get player data object and change the blocks placed value
		pUpdate = pMap.get(placer.getName());
		if(evt.getBlock().getType().isBlock()) {
			int cnt = pUpdate.getPlaced();
			pUpdate.setPlaced(cnt + 1);
			
			// Update the value in the map for this player
			pMap.put(placer.getName(), pUpdate);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt) {
		// Get the player that broke a block
		Player breaker = evt.getPlayer();
		
		// Get player data object and change the blocks broken value
		pUpdate = pMap.get(breaker.getName());
		if(evt.getBlock().getType().isBlock()) {
			int cnt = pUpdate.getBroke();
			pUpdate.setBroke(cnt + 1);
			
			// Update the value in the map for this player
			pMap.put(breaker.getName(), pUpdate);
		}
	}
}
