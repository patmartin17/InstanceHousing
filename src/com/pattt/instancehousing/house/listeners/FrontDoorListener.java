package com.pattt.instancehousing.house.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.events.OfferHouseEvent;
import com.pattt.instancehousing.events.PlayerEnterHouseEvent;
import com.pattt.instancehousing.events.PlayerLeaveHouseEvent;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.managers.DoorManager;
import com.pattt.instancehousing.profile.Profile;


public class FrontDoorListener implements Listener
{
	private InstanceHousing plugin = InstanceHousing.getInstance();

	/*
	 * Write in comments what each listener/method does for organization.
	 * 
	 * NOTE (PROBLEMS): 
	 * - etnering house doesnt work on reload. you have to open the door first and let it close and then try agian. 
	 * - Make the door a player send change door which will be difficult but do it instead. 
	 * - DIdnt check z at all so you can walk past that x on any part of the world and enter the house make sure it checks z and make sure we have house boundaires other than the door.
	 */

	@EventHandler
	public void onPlayerOpenDoor(PlayerInteractEvent event)
	{
		Block clickedBlock = event.getClickedBlock();
		Player player = event.getPlayer();

		if(clickedBlock == null)
			return;

		if(DoorManager.isDoor(clickedBlock))
		{
			Location frontDoorLocation = clickedBlock.getLocation();
			Door door = (Door) clickedBlock.getBlockData();
			House house = House.getHouseByFrontDoor(frontDoorLocation);
			
			Profile profile = Profile.getExistingByUUID(player.getUniqueId());

			if(!door.isOpen() && profile.getHouse() == null)
			{
				if(!doesPlayerOwnHouse(player, House.getHouseByFrontDoor(frontDoorLocation)))
				{
					if(Profile.getByPlayer(player) != null)
					{
						PlayerHouse.loadNewPlayerHouse(profile, house);
					}
					else
					{
						Bukkit.getPluginManager().callEvent(new OfferHouseEvent(player, clickedBlock, profile.getHouse()));
						return;	
					}
				}

				if(profile.getIsEnteringHouse() == null) 
				{
					profile.setIsEnteringHouse(house);

					new BukkitRunnable() 
					{
						@Override
						public void run() 
						{
							/*
							 * This is the source of the inability to enter and exit quickly add if statements that make this stop.
							 */
							profile.setIsEnteringHouse(null);

							DoorManager.closeDoor(clickedBlock, player);
						}

					}.runTaskLater(plugin, 60L);	
				}
			}
			else if(!door.isOpen() && profile.getHouse() != null)
			{
				if(profile.getIsExitingHouse() == null) 
				{
					profile.setIsExitingHouse(house);
					
					new BukkitRunnable() 
					{
						@Override
						public void run() 
						{
							
							profile.setIsExitingHouse(null);

							DoorManager.closeDoor(clickedBlock, player);
						}

					}.runTaskLater(plugin, 60L);	
				}
			}
		}

		/*
		 * when the player has opened the front door they are added to a group of those who are in door limbo only if they arent already in their house
		 * and then that group is checked by playermoveevent for those three seconds that they are in the new group and if they do not enter the door
		 * the door closes. This way we can check when they actually enter the house before loading the furniture. And the door can remain shut.
		 * The door closes whether or not they are inside or outside the house after 3 seconds.
		 */
		//check if they are inside the house already and then send playerexithousevent or if they arent in side the house player enter house event.
		//check whether they are within the bounds of the house and whether they are within the hashmap of those inside the hosue. 
		//if they find a way to escape and they immediatly run through the front door again were going to ignore the front door load listener and rely
		//solely on the 10 second check listener.
	}

	@EventHandler
	public void onPlayerPassFrontDoor(PlayerMoveEvent event)
	{

		/*
		 * MAKE THIS ENTIRE THING WORK WITH ANY HOUSE BY USING HOUSE OBJECTS! RIGHT NOW IT ONLY WORKS FOR ONCE HOUSE THAT WERE TESTING ON.
		 */
		Player player = event.getPlayer();
		Profile profile = Profile.getExistingByUUID(player.getUniqueId());



		if(profile.getIsEnteringHouse() != null)
		{
			Location doorLocation = profile.getIsEnteringHouse().getFrontDoor();

			if(DoorManager.hasPlayerPassed(player.getLocation(), doorLocation, profile.getIsEnteringHouse().getDoorDirection(), true))
			{
				//Find a ring sound for entrance. Check if the door is an active front door.
				/*
				 * CLOSE DOOR BEHIND PERSON IMMEDIATLY AFTER ENTRANCE.
				 */

				DoorManager.closeDoor(doorLocation.getBlock(), player);

				event.getPlayer().sendMessage("You are now in your house.");
				profile.setIsEnteringHouse(null);
				profile.setHouse(House.getHouseByFrontDoor(doorLocation));

				//NOW CALLING ENTRANCE EVENT
				Bukkit.getPluginManager().callEvent(new PlayerEnterHouseEvent(player, profile.getHouse()));
			}

		}
		if(profile.getIsExitingHouse() != null)
		{

			
			Location doorLocation = profile.getIsExitingHouse().getFrontDoor();
			
			if(DoorManager.hasPlayerPassed(player.getLocation(), doorLocation, profile.getIsExitingHouse().getDoorDirection(), false))
			{
				//Find a ring sound for entrance. Check if the door is an active front door.
				/*
				 * CLOSE DOOR BEHIND PERSON IMMEDIATLY AFTER ENTRANCE.
				 */

				DoorManager.closeDoor(doorLocation.getBlock(), player);
				
				Furniture.updateFurniture(player);

				event.getPlayer().sendMessage("You have left your house.");
				profile.setIsExitingHouse(null);
				Bukkit.getPluginManager().callEvent(new PlayerLeaveHouseEvent(player, profile.getHouse()));
				profile.setHouse(null);

				//NOW CALLING EXIT EVENT
			}

		}

	}

	private boolean doesPlayerHaveHouseFile(Player player)
	{
		if(Profile.getByPlayer(player).getOwnedHouses() == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private boolean doesPlayerOwnHouse(Player player, House house)
	{
		Profile profile = Profile.getByPlayer(player);
		
		if(!doesPlayerHaveHouseFile(player))
		{
			return false;
		}
		if(profile.getOwnedHouses().contains(PlayerHouse.getPlayerHouse(profile, house)))
		{
			return true;
		}		
		return false;
	}

}
