package com.pattt.instancehousing.house.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.profile.Profile;


public class BlockManager 
{
	private static InstanceHousing plugin = InstanceHousing.getInstance();

	/*
	 * If furniture is not already loaded sending this has no effect. 
	 */
	public static void sendSingleBlockChange(Player player, Location location, BlockData blockData)
	{
		Profile profile = Profile.getByPlayer(player);
		
		if(profile.getHouse() != null)
		{
			House house = profile.getHouse();
			PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, house);
			
			playerHouse.getFurniture().getReloadingBlocks().add(location);
			
			new BukkitRunnable() 
			{
				@Override
				public void run() 
				{
					player.sendBlockChange(location, blockData);
					playerHouse.getFurniture().reloadingBlocks.remove(location);
				}
			}.runTaskLater(plugin, 0L);	
		}
	}
	
	public static void reloadBlock(Player player, Location location, Material material, PlayerHouse playerHouse)
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				player.sendBlockChange(location, material.createBlockData());
				
				playerHouse.getFurniture().reloadingBlocks.remove(location);
			}		
		}.runTaskLater(plugin, 0L);
	}
	

	public static void loadBlockFurniture(Player player, Furniture furniture)
	{
		PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(Profile.getExistingByUUID(player.getUniqueId()), furniture.getHouse());
		
		new BukkitRunnable() {

			@Override
			public void run() {
				for(Location locations : furniture.getFurnitureList().keySet())
				{
					BlockData data = furniture.getFurnitureList().get(locations);
					
					player.sendBlockChange(locations, data);
				}
				playerHouse.setFurnitureLoaded(true);
			}
		}.runTaskLater(plugin, 1L);
	}

	public static void unloadBlockFurniture(Player player, Furniture furniture)
	{
		PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(Profile.getExistingByUUID(player.getUniqueId()), furniture.getHouse());
		
		new BukkitRunnable() {

			@Override
			public void run() {
				for(Location locations : furniture.getFurnitureList().keySet())
				{
					player.sendBlockChange(locations, Material.AIR.createBlockData());
				}
				playerHouse.setFurnitureLoaded(false);
			}
		}.runTaskLater(plugin, 0L);
	}
}
