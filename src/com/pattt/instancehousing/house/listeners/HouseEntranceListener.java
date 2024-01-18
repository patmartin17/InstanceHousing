package com.pattt.instancehousing.house.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.pattt.instancehousing.events.PlayerEnterHouseEvent;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.managers.BlockManager;
import com.pattt.instancehousing.profile.Profile;

public class HouseEntranceListener implements Listener
{	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onHouseEntrance(PlayerEnterHouseEvent event)
	{
		//make the player invisbile to the rest of the public outside of the houses. 
		//load the players furniture.

		Player player = event.getPlayer();

		/*
		 * Setting player invisible for all online players. WE ALSO NEED TO HAVE A JOIN EVENT FOR NEW PLAYERS TO MAKE THE PLAYERS IN THE HOUSE INVISIBLE
		 * TO THEM TOO!! DO NOT FORGET THIS!
		 */
		for(Player players : Bukkit.getOnlinePlayers())
		{
			players.hidePlayer(player);
		}

		/*
		 * Load all furniture from the Funarture object in PlayerHouse
		 */
		Profile profile = Profile.getExistingByUUID(player.getUniqueId());

		PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, event.getHouse());

		Furniture furniture = playerHouse.getFurniture();

		BlockManager.loadBlockFurniture(player, furniture);
	}
}
