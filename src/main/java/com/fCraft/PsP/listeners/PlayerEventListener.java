package com.fCraft.PsP.listeners;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.fCraft.PsP.PlayerLocation;
import com.fCraft.PsP.PsP;

public class PlayerEventListener implements Listener {

	/** Map of online players, potential AFKers, for the server **/
	ConcurrentHashMap<Player, PlayerLocation> onlinePlayers = PsP.getOnlinePlayers();

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		updateActivity(p);
	}

	@EventHandler
	public void onPlayerEntityInteraction(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		updateActivity(p);
	}
	
	@EventHandler
	public void onPlayerInteractaion(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		updateActivity(p);
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		Entity entity = event.getDamager();
		
		if(entity instanceof Player)
			updateActivity((Player) entity);
	}
	
	private void updateActivity(Player p) {
		PlayerLocation pLoc = onlinePlayers.get(p);
		
		if(pLoc.isActive()) {
			return;
		} else {
			pLoc.setActive(true);
			onlinePlayers.put(p, pLoc);
		}
	}
}
