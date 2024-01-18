package com.pattt.instancehousing.house.creation;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.managers.DoorManager;
import com.pattt.instancehousing.profile.Admin;
import com.pattt.instancehousing.tools.DirectionTool;
import com.pattt.instancehousing.tools.LocationTool;
import com.pattt.instancehousing.tools.MessageTool;

public class HouseCreationListeners implements Listener
{
	private InstanceHousing plugin = InstanceHousing.getInstance();

	Location firstPosition;
	
	Location secondPosition;
	
	Location frontDoorLocation;
	
	String doorDirection;
	
	int xMax = 100;
	int yMax = 100;
	int zMax = 100;
	
	/*
	 * Check for things like are all locations set before shift right click. Do a confirm right click or something. Make this more fool proof.
	 */
	
	/*
	 * Update all blocks that were sendBlockChange blocks.
	 */

	@EventHandler
	public void onHouseClick(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		Block block = event.getClickedBlock();

		Admin adminProfile = Admin.getByUUID(player.getUniqueId());
		
		if(adminProfile == null)
			return;

		if(!adminProfile.isInHouseCreation)
			return;
		
		if(!Admin.houseCreationMap.containsKey(player))
			return;
		
		if(!player.getInventory().getItemInMainHand().equals(HouseCreationCommands.getHouseWand()))
			return;
		
		String houseName = Admin.houseCreationMap.get(player);

		if(event.getAction().toString().contains("LEFT_CLICK_BLOCK"))
		{	
			event.setCancelled(true);
			
			if(player.isSneaking())
			{
				if(DoorManager.isDoorBlock(event.getClickedBlock()))
				{
					frontDoorLocation = event.getClickedBlock().getLocation();	
					
					doorDirection = DirectionTool.getDirection(player);
					
					player.sendMessage(MessageTool.color(MessageTool.prefix + "&aYou have selected the front door for &f" + houseName + "&a."));
				}
				else
				{
					player.sendMessage(MessageTool.color(MessageTool.prefix + "&cTo select the house's front door, you must crouch and left click the top of the door."));
				}
			}
			else
			{	
				if(firstPosition != null)
				{
					sendBlockChangeLater(player, firstPosition, firstPosition.getBlock().getType());	
				}
				sendBlockChangeLater(player, event.getClickedBlock().getLocation(), Material.DIAMOND_BLOCK);	
				
				firstPosition = block.getLocation();
				
				player.sendMessage(MessageTool.color(MessageTool.prefix + "&aYou have selected &fposition 1 &afor &f" + houseName + "&a."));
				
			}
		}
		else if(event.getAction().toString().contains("RIGHT") && event.getHand().equals(EquipmentSlot.HAND))
		{
			event.setCancelled(true);
			
			if(player.isSneaking())
			{

				if(firstPosition == null || secondPosition == null || frontDoorLocation == null)
				{
					player.sendMessage(MessageTool.color(MessageTool.prefix + "&cAssure that all positions have been selected before creating the house."));
					return;	
				}
				
				if(!checkMaxSize(firstPosition, secondPosition))
				{
					player.sendMessage(MessageTool.color(MessageTool.prefix + "&cYou have exceeded the max size of house creation: " + xMax + "x" + yMax + "x" + zMax));
					return;	
				}
				
				adminProfile.setInHouseCreation(false);
				
				createHouse(player, houseName, firstPosition, secondPosition, frontDoorLocation, doorDirection);
				
				player.sendMessage(MessageTool.color(MessageTool.prefix + "&aYou have created a house called &f" + houseName + "&a."));

			}
			else
			{
				event.setCancelled(true);
				
				if(block == null)
				{ 
					player.sendMessage(MessageTool.color(MessageTool.prefix + "&cYou must select an exisiting block."));
				}
				else
				{
					if(secondPosition != null)
					{
						sendBlockChangeLater(player, secondPosition, secondPosition.getBlock().getType());	
					}
					sendBlockChangeLater(player, event.getClickedBlock().getLocation(), Material.DIAMOND_BLOCK);	
					
					secondPosition = block.getLocation();
					
					player.sendMessage(MessageTool.color(MessageTool.prefix + "&aYou have selected &fposition 2 &afor &f" + houseName + "&a."));		
				}
			}
		}	
	}
	
	private boolean checkMaxSize(Location firstPosition, Location secondPosition)
	{
		Vector min = Vector.getMinimum(firstPosition.toVector(), secondPosition.toVector());
		Vector max = Vector.getMaximum(firstPosition.toVector(), secondPosition.toVector());
		
		if((max.getBlockX() - min.getBlockX()) > xMax)
		{
			return false;
		}
		if((max.getBlockY() - min.getBlockY()) > yMax)
		{
			return false;
		}
		if((max.getBlockZ() - min.getBlockZ()) > zMax)
		{
			return false;
		}
		return true;
	}
	
	private void createHouse(Player player, String houseName, Location firstPosition, Location secondPosition, Location frontDoorLocation, String doorDirection)
	{	
		/*
		 * Creates the new house object, then defines its file location and configuration. 
		 */
		House newHouse = new House(houseName, player.getWorld(), firstPosition, secondPosition, frontDoorLocation, doorDirection);
		
		File file = new File(plugin.getDataFolder(), "/houses/" + houseName + ".yml");
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		newHouse.setFile(file);
		newHouse.setConfiguration(configuration);
		
		/*
		 * Saves the house information to the file that we have previously created and saved. 
		 */
		newHouse.saveHouseToFile(houseName, firstPosition, secondPosition, frontDoorLocation, doorDirection);
		newHouse.saveCurrentBlocks(player.getWorld(), firstPosition, secondPosition, houseName);
		
		/*
		 * Defines the house specific configuration section after having saved the house and its current blocks. 
		 */
		ConfigurationSection houseConfig = configuration.getConfigurationSection(houseName);
		newHouse.setHouseConfig(houseConfig);
		
		/*
		 * Adds meta data to the current blocks found in the previous method. 
		 */
		newHouse.addFixedMetadata();
		
		/*
		 * Runs player specific block updates and animations. 
		 */
		updateUsedBlocks(player);
		
		Location blockBelow = new Location(frontDoorLocation.getWorld(), frontDoorLocation.getX(), frontDoorLocation.getY() - 1, frontDoorLocation.getZ());
		
		playEffectOnCreation(frontDoorLocation.getWorld(), blockBelow);
		
	}

	private void sendBlockChangeLater(Player player, Location location, Material material)
	{
		new BukkitRunnable() 
		{

			@Override
			public void run() 
			{
				player.sendBlockChange(location, material.createBlockData());
			}

		}.runTaskLater(plugin, 1L);
	}
	
	private void updateUsedBlocks(Player player)
	{
		sendBlockChangeLater(player, firstPosition, firstPosition.getBlock().getType());	
		sendBlockChangeLater(player, secondPosition, secondPosition.getBlock().getType());	
	}
	
	private void playEffectOnCreation(World world, Location location)
	{	
		Location newLocation = LocationTool.getCenter(world, location);
		
		for(int i = 0; i <= 10; i++)
		{
			world.spawnParticle(Particle.ENCHANTMENT_TABLE, newLocation, 64);	
		}
		
		world.playSound(newLocation, Sound.BLOCK_IRON_DOOR_OPEN, 3.0F, 0.5F);
	}
}
