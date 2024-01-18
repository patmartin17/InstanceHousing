package com.pattt.instancehousing.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.profile.Profile;

public class PlayerQuitListeners implements Listener
{
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getExistingByUUID(player.getUniqueId());
		if(profile.getOwnedHouses() != null)
		{
			/*
			 * Updates newly placed furniture, and unloads playerHouses in Profiles class. 
			 * 
			 * NEEDS: check if furniture properly unloads when they log off. 
			 */
			Furniture.updateFurniture(player);
			profile.unloadPlayerHouses();
		}
	}
}
