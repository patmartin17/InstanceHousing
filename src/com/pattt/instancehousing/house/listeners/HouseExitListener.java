package com.pattt.instancehousing.house.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.pattt.instancehousing.events.PlayerLeaveHouseEvent;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.managers.BlockManager;
import com.pattt.instancehousing.profile.Profile;


public class HouseExitListener implements Listener
{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onHouseExit(PlayerLeaveHouseEvent event)
	{
		//make them visible to the public again. 
		//unload pets and furniture.

		Player player = event.getPlayer();

		for(Player players : Bukkit.getOnlinePlayers())
		{
			players.showPlayer(player);
		}

		/*
		 * Unload all furniture from the Funarture object in PlayerHouse
		 */
		Profile profile = Profile.getExistingByUUID(player.getUniqueId());
		
		PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, event.getHouse());
		
		Furniture furniture = playerHouse.getFurniture();
		
		BlockManager.unloadBlockFurniture(player, furniture);
	}
}
