package com.pattt.instancehousing.house.managers;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;

import com.pattt.instancehousing.house.House;

public class DoorManager 
{
	public static boolean isDoor(Block block)
	{
		if(block.getType().toString().contains("_DOOR"))
		{
			if(House.frontDoorList.containsKey(block.getLocation()))
				return true;
		}
		return false;	
	}
	
	public static void closeDoor(Block block, Player player)
	{
		Door door = (Door) block.getBlockData();

		if(door.isOpen())
		{
			door.setOpen(false);
			block.setBlockData(door);
			player.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 10, 1);	
		}
	}
	
	public static boolean isDoorBlock(Block block)
	{
		if(block.getType().toString().contains("_DOOR"))
			return true;
		return false;	
	}
	
	public static boolean hasPlayerPassed(Location playerLocation, Location entranceLocation, String direction, boolean entering)
	{
		/*
		 * Return the exact point where the player enters their house. Determine this by finding the default way that the door faces
		 * and the direction they would have to walk to enter the house. Subtract or add .5 to that and that should be the entered location. 
		 */
		
		if(direction.equals("NORTH"))
		{
			if(entering)
			{
				if(playerLocation.getZ() <= (entranceLocation.getBlockZ() - .5))	
					return true;	
			}
			else
			{
				if(playerLocation.getZ() >= (entranceLocation.getBlockZ() + .5))	
					return true;
			}
		}
		else if(direction.equals("SOUTH"))
		{
			if(entering)
			{	
				if(playerLocation.getZ() >= (entranceLocation.getBlockZ() + .5))	
					return true;
			}
			else
			{
				if(playerLocation.getZ() <= (entranceLocation.getBlockZ() - .5))	
					return true;	
			}
		}
		else if(direction.equals("EAST"))
		{
			if(entering)
			{	
				if(playerLocation.getX() >= (entranceLocation.getBlockX() + .5))	
					return true;
			}
			else
			{
				if(playerLocation.getX() <= (entranceLocation.getBlockX() - .5))	
					return true;	
			}
		}
		else if(direction.equals("WEST"))
		{	
			if(entering)
			{	
				if(playerLocation.getX() <= (entranceLocation.getBlockX() - .5))	
					return true;	
			}
			else
			{
				if(playerLocation.getX() >= (entranceLocation.getBlockX() + .5))	
					return true;
			}
		}
		
		return false;
	}
}
