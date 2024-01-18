package com.pattt.instancehousing.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.profile.Profile;


public class PlayerJoinListeners implements Listener
{
	private InstanceHousing plugin = InstanceHousing.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				Profile profile = Profile.getExistingByUUID(player.getUniqueId());
				if(profile.getOwnedHouses() == null && !profile.housesLoaded)
					Profile.loadPlayerHouses(profile);
			}
		}.runTaskLater(plugin, 1L);
	}
}
