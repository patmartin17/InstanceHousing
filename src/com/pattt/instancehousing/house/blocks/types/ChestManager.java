package com.pattt.instancehousing.house.blocks.types;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.blocks.InteractableBlock;
import com.pattt.instancehousing.house.managers.BlockManager;
import com.pattt.instancehousing.profile.Profile;

public class ChestManager extends DummyBlock
{	
	private static InstanceHousing plugin = InstanceHousing.getInstance();
	
	private Material chest = Material.CHEST;
	
	public String getChestDirection(BlockData blockData)
	{
		String[] firstSplit = blockData.getAsString().split(",");
		String[] equalsData = firstSplit[0].split("=");
		return equalsData[1];	
	}
	
	
	/*
	 * Chest Connection Methods
	 */
	public void handleConnection(Player player, Furniture furniture, Location location, BlockData blockData)
	{
		if(player.isSneaking())
		{
			if(getBlockHandler().clickedInteractableBlock(player))
			{
				Location clickedBlockLocation = getBlockHandler().getClickedBlockLocation(player);
				Bukkit.broadcastMessage(clickedBlockLocation.toString());
				Material clickedBlock = furniture.getFurnitureList().get(clickedBlockLocation).getMaterial();
				
				if(!clickedBlock.equals(chest))
				{
					getBlockHandler().placeFurnitureBlock(player, furniture, location, blockData);
				}
				else
				{
					if(getBlockHandler().getClickedBlockFace(player).toString().equals("UP") || getBlockHandler().getClickedBlockFace(player).toString().equals("DOWN"))
					{
						getBlockHandler().placeFurnitureBlock(player, furniture, location, blockData);
					}
				}
			}
			else
			{
				getBlockHandler().placeFurnitureBlock(player, furniture, location, blockData);
			}
		}
		else
		{
			String direction = getChestDirection(blockData);

			Location[] leftAndRightLocations = determineLeftAndRight(direction, player, location);
			
			handleChestSides(player, leftAndRightLocations[0], leftAndRightLocations[1], direction, location, blockData, null);	
		}
	}
	public void handleChestSides(Player player, Location left, Location right, String direction, Location location, BlockData blockData, List<ItemStack> currentItems)
	{
		Profile profile = Profile.getByPlayer(player);
		House house = profile.getHouse();
		PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, house);
		Furniture furniture = playerHouse.getFurniture();
		
		File file = new File(plugin.getDataFolder() + "/storage/" + player.getUniqueId() + ".yml");
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		
		if(furniture.getFurnitureList().containsKey(left))
		{
			Material material = furniture.getFurnitureList().get(left).getMaterial();
			if(material.equals(chest))
			{
				BlockData leftBlockData = furniture.getFurnitureList().get(left);
				
				if(!leftBlockData.getAsString().contains("single"))
					return;

				String[] firstLeftSplit = leftBlockData.getAsString().split(",");
				String[] equalsLeftData = firstLeftSplit[0].split("=");

				if(equalsLeftData[1].equals(direction))
				{
					eraseChestData(file, configuration, playerHouse, left);
					handleChestPlacement(player, furniture, left, location, leftBlockData, blockData, currentItems);
					return;
				}
			}
		}
		if(furniture.getFurnitureList().containsKey(right))
		{
			Material material = blockData.getMaterial();
			if(material.equals(chest))
			{
				BlockData rightBlockData = furniture.getFurnitureList().get(right);

				if(!rightBlockData.getAsString().contains("single"))
					return;

				String[] firstRightSplit = rightBlockData.getAsString().split(",");
				String[] equalsRightData = firstRightSplit[0].split("=");


				if(equalsRightData[1].equals(direction))
				{
					eraseChestData(file, configuration, playerHouse, right);
					handleChestPlacement(player, furniture, location, right, blockData, rightBlockData, currentItems);							
				}
			}
		}
	}
	
	public void handleChestPlacement(Player player, Furniture furniture, Location leftBlockLocation, Location rightBlockLocation, BlockData leftBlock, BlockData rightBlock, List<ItemStack> currentItems)
	{	
		Profile profile = Profile.getByPlayer(player);
		House house = profile.getHouse();
		
		String leftBlockString = leftBlock.getAsString().replace("single", "right");
		String rightBlockString = rightBlock.getAsString().replace("single", "left");

		BlockData newLeftBlockData = Bukkit.createBlockData(leftBlockString);
		BlockData newRightBlockData = Bukkit.createBlockData(rightBlockString);

		furniture.getFurnitureList().remove(leftBlockLocation);
		furniture.getFurnitureList().remove(rightBlockLocation);

		furniture.getFurnitureList().put(leftBlockLocation, newLeftBlockData);
		furniture.getFurnitureList().put(rightBlockLocation, newRightBlockData);
		
		Inventory inventory = Bukkit.createInventory(null, 54);
		
		if(currentItems != null && !currentItems.isEmpty())
		{
			for(int i = 0; i < 27; i++)
			{
				inventory.setItem(i, currentItems.get(i));
			}	
		}
		
		InteractableBlock.saveChestData(player, house, leftBlockLocation, rightBlockLocation, inventory.getContents());
		
		BlockManager.sendSingleBlockChange(player, leftBlockLocation, newLeftBlockData);
		BlockManager.sendSingleBlockChange(player, rightBlockLocation, newRightBlockData);
	}
	
	public void eraseChestData(File file, YamlConfiguration configuration, PlayerHouse playerHouse, Location location)
	{
		String storageEntry = location.getBlockX()  + "," + location.getBlockY() + "," + location.getBlockZ();
		
		configuration.set(playerHouse.getHouse().getName() + ".chests." + storageEntry, null);
		try 
		{
			configuration.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	public Location[] determineLeftAndRight(String direction, Player player, Location location)
	{
		Location[] sides = null;
		
		switch (direction)
		{
		case "north": 
		{
			Location left = new Location(player.getWorld(), location.getX() + 1, location.getY(), location.getZ());
			Location right = new Location(player.getWorld(), location.getX() - 1, location.getY(), location.getZ());
			
			sides = new Location[]{left, right};
			
			return sides;
		}
		case "south": 
		{
			Location left = new Location(player.getWorld(), location.getX() - 1, location.getY(), location.getZ());
			Location right = new Location(player.getWorld(), location.getX() + 1, location.getY(), location.getZ());
			
			sides = new Location[]{left, right};
			
			return sides;
		}
		case "east": 
		{
			Location left = new Location(player.getWorld(), location.getX(), location.getY(), location.getZ() + 1);
			Location right = new Location(player.getWorld(), location.getX(), location.getY(), location.getZ() - 1);

			sides = new Location[]{left, right};
			
			return sides;
		}
		case "west": 
		{
			Location left = new Location(player.getWorld(), location.getX(), location.getY(), location.getZ() - 1);
			Location right = new Location(player.getWorld(), location.getX(), location.getY(), location.getZ() + 1);
			
			sides = new Location[]{left, right};
			
			return sides;
		}
		default: return new Location[]{};
		}
	}
}
