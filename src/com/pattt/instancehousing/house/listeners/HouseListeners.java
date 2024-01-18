package com.pattt.instancehousing.house.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.blocks.BlockHandler;
import com.pattt.instancehousing.house.blocks.ConnectableBlock;
import com.pattt.instancehousing.house.blocks.InteractableBlock;
import com.pattt.instancehousing.house.managers.BlockManager;
import com.pattt.instancehousing.profile.Profile;
import com.pattt.instancehousing.tools.MessageTool;


public class HouseListeners implements Listener
{
	private static InstanceHousing plugin = InstanceHousing.getInstance();
	private static BlockHandler blockHandler = plugin.getBlockHandler();
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onFakeBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getExistingByUUID(player.getUniqueId());

		if(profile.getHouse() != null)
		{
			/*
			 * Checks if the player is standing inside the block they are trying to place.
			 */
			if(areLocationIntegersEqual(newBlockLocation(event.getBlockAgainst().getLocation(), blockHandler.getClickedBlockFaceMap().get(player)), player.getLocation()))
				return;
			if(areLocationIntegersEqual(newBlockLocation(event.getBlockAgainst().getLocation(), blockHandler.getClickedBlockFaceMap().get(player)), player.getLocation().add(0, 1, 0)))
				return;
			
			//ADD UNPLACABLE BLOCKS
			event.setCancelled(true);

			ItemStack item = player.getItemInHand();

			if(!player.getGameMode().equals(GameMode.CREATIVE))
				item.setAmount(item.getAmount() -1);

			player.updateInventory();

			/*
			 * Used to prevent the FakeBlockInteractionListener from disabling placement of a new block.
			 */
			blockHandler.getPlacingBlockMap().add(player);

			/*
			 * Add save furniture listener
			 */

			PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, profile.getHouse());

			String blockName = event.getBlock().getType().name().toUpperCase();
			
			/*
			 * If the block that the player clicked against is already a fake block we handle placement differently. 
			 */
			if(playerHouse.getFurniture().getFurnitureList().containsKey(event.getBlockAgainst().getLocation()))
			{		
				if(isConnectableBlock(player, playerHouse, blockName, newBlockLocation(event.getBlockAgainst().getLocation(), blockHandler.getClickedBlockFaceMap().get(player)), event.getBlock().getBlockData()))
				{
					blockHandler.getClickedBlockFaceMap().remove(player);
				}
				else
				{
					BlockManager.sendSingleBlockChange(player, newBlockLocation(event.getBlockAgainst().getLocation(), blockHandler.getClickedBlockFaceMap().get(player)), event.getBlock().getBlockData());
					playerHouse.getFurniture().getFurnitureList().put(newBlockLocation(event.getBlockAgainst().getLocation(), blockHandler.getClickedBlockFaceMap().get(player)), event.getBlock().getBlockData());

					blockHandler.getClickedBlockFaceMap().remove(player);	
				}
			}
			else
			{	
				if(isConnectableBlock(player, playerHouse, blockName, event.getBlock().getLocation(), event.getBlock().getBlockData()))
					return;
				
				playerHouse.getFurniture().getFurnitureList().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
				BlockManager.sendSingleBlockChange(player, event.getBlock().getLocation(), event.getBlock().getBlockData());	
			}

			new BukkitRunnable() 
			{
				@Override
				public void run() 
				{
					blockHandler.getPlacingBlockMap().remove(player);
				}
			}.runTaskLater(plugin, 1L);

		}
	}
	
	/*
	 * Checks if a given block is a type of block that can be connected to surrounding blocks
	 * 
	 * and handles such connection according to a registered method. 
	 */
	public boolean isConnectableBlock(Player player, PlayerHouse playerHouse, String blockName, Location location, BlockData blockData) 
	{	
		for(ConnectableBlock blocks : ConnectableBlock.values())
		{
			if(blocks.toString().equals(blockName))
			{
				ConnectableBlock connectableBlock = ConnectableBlock.valueOf(blockName);
				
				playerHouse.getFurniture().getFurnitureList().put(location, blockData);
				
				connectableBlock.handleInteraction(player, location, blockData);
				return true;
			}		
		}
		return false;
	}

	@EventHandler
	public void onRealBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		Block block = event.getBlock();

		if(profile.getHouse() != null)
		{
			PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, profile.getHouse());

			/*
			 * Maybe make fake block class for checking if this exists. 
			 */
			if(block.hasMetadata("PresetBlock"))
			{
				player.sendMessage(MessageTool.color("&cYou cannot break house blocks."));
				event.setCancelled(true);
			}
			else if(playerHouse.getFurniture().getFurnitureList().containsKey(event.getBlock().getLocation()))
			{
				BlockManager.sendSingleBlockChange(player, event.getBlock().getLocation(), Material.AIR.createBlockData());
				playerHouse.getFurniture().getFurnitureList().remove(event.getBlock().getLocation());
			}
		}
	}

	@EventHandler
	public void onBlockClick(PlayerInteractEvent event)
	{
		Profile profile = Profile.getByPlayer(event.getPlayer());		
		
		if(profile.getHouse() != null)
		{
			Furniture furniture = PlayerHouse.getPlayerHouse(profile, profile.getHouse()).getFurniture();
			if(event.getAction().toString().contains("RIGHT"))
			{
				if(furniture.getFurnitureList().containsKey(event.getClickedBlock().getLocation()))
				{
					String blockName = furniture.getFurnitureList().get(event.getClickedBlock().getLocation()).getMaterial().name();

					if(isInteractableBlock(blockName))
					{
						blockHandler.getClickedBlockMap().put(event.getPlayer(), event.getClickedBlock().getLocation());
						blockHandler.getClickedBlockFaceMap().put(event.getPlayer(), event.getBlockFace());	
						
						new BukkitRunnable() 
						{
							@Override
							public void run() 
							{
								blockHandler.getClickedBlockMap().remove(event.getPlayer());
							}
						}.runTaskLater(plugin, 1L);
					}	
				}
				else
				{
					blockHandler.getClickedBlockFaceMap().put(event.getPlayer(), event.getBlockFace());	
				}
			}
		}
	}
	
	/*
	 * Move this to InteractableBlock class
	 */
	public boolean isInteractableBlock(String blockName)
	{
		for(InteractableBlock blocks : InteractableBlock.values())
		{
			if(blocks.toString().contains(blockName))
			{
				return true;
			}		
		}
		return false;
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);

		if(profile.getHouse() != null)
		{
			if(event.getReason().contains("Flying is not enabled on this server"))
				event.setCancelled(true);
		}
	}

	public static Location newBlockLocation(Location location, BlockFace face)
	{
		Location newBlockLocation;

		switch (face.toString()) 
		{
		case "UP":  newBlockLocation = new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ());
		return newBlockLocation;

		case "DOWN":  newBlockLocation = new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ());
		return newBlockLocation;

		case "NORTH":  newBlockLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() - 1);
		return newBlockLocation;

		case "SOUTH":  newBlockLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + 1);
		return newBlockLocation;

		case "EAST":  newBlockLocation = new Location(location.getWorld(), location.getX() + 1, location.getY(), location.getZ());
		return newBlockLocation;

		case "WEST":  newBlockLocation = new Location(location.getWorld(), location.getX() - 1, location.getY(), location.getZ());
		return newBlockLocation;

		default: newBlockLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
		return newBlockLocation;
		}
	}

	private boolean areLocationIntegersEqual(Location locationOne, Location locationTwo)
	{
		if(locationOne.getBlockX() != locationTwo.getBlockX())
			return false;
		if(locationOne.getBlockY() != locationTwo.getBlockY())
			return false;
		if(locationOne.getBlockZ() != locationTwo.getBlockZ())
			return false;
		return true;
	}
}
