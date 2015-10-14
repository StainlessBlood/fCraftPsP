package com.fCraft.PsP.listeners;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.fCraft.PsP.PlayerData;
import com.fCraft.PsP.PsP;

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
			long cnt = pUpdate.getPlaced();
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
			long cnt = pUpdate.getBroken();
			pUpdate.setBroken(cnt + 1);
			
			// Update the value in the map for this player
			pMap.put(breaker.getName(), pUpdate);
		}
	}
}
