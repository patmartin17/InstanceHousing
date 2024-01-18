package com.pattt.instancehousing.house;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.pattt.instancehousing.InstanceHousing;

import lombok.Getter;
import lombok.Setter;

public class House 
{	
	private static InstanceHousing plugin = InstanceHousing.getInstance();
	
	/*
	 * make these protected?
	 */
	@Getter protected static Map<String, House> loadedHouses = new HashMap<String, House>();
	@Getter public static Map<Location, House> frontDoorList = new HashMap<Location, House>();
	@Getter @Setter public String name;
	@Getter @Setter public World world;
	@Getter @Setter public Location minLocation;
	@Getter @Setter public Location maxLocation;
	@Getter @Setter public Location frontDoor;
	@Getter @Setter public String doorDirection;
	@Getter @Setter public File file;
	@Getter @Setter public YamlConfiguration configuration;
	@Getter @Setter public ConfigurationSection houseConfig;
	
	
	public House(String name, World world, Location minLocation, Location maxLocation, Location frontDoor, String doorDirection)
	{
		setName(name);
		
		setWorld(world);
		setMinLocation(minLocation);
		setMaxLocation(maxLocation);
		setFrontDoor(frontDoor);
		setDoorDirection(doorDirection);
		
		loadedHouses.put(name, this);
		frontDoorList.put(frontDoor, this);
	}
	
	public static House getHouseByName(String name)
	{	
		if(loadedHouses.containsKey(name))
		{
			return loadedHouses.get(name);
		}
		return null;
	}
	
	public static void getHouseByLocation()
	{
		/*
		 * loop through all 
		 */
	}
	
	public static House getHouseByFrontDoor(Location location)
	{
		if(frontDoorList.containsKey(location))
		{
			return frontDoorList.get(location);
		}
		return null;
	}
	
	public static void loadHouse(String string)
	{
		File file = new File(plugin.getDataFolder(), "/houses/" + string + ".yml");
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		ConfigurationSection houseConfig = configuration.getConfigurationSection(string);
		
		Location firstLocation = new Location(Bukkit.getWorld(houseConfig.getString("world")), 
		houseConfig.getInt("first_location.x"), 
		houseConfig.getInt("first_location.y"), 
		houseConfig.getInt("first_location.z"));
		
		Location secondLocation = new Location(Bukkit.getWorld(houseConfig.getString("world")), 
		houseConfig.getInt("second_location.x"), 
		houseConfig.getInt("second_location.y"), 
		houseConfig.getInt("second_location.z"));
		
		Location frontDoorLocation = new Location(Bukkit.getWorld(houseConfig.getString("world")), 
		houseConfig.getInt("front_door_location.x"), 
		houseConfig.getInt("front_door_location.y"), 
		houseConfig.getInt("front_door_location.z"));

		
		String doorDirection = houseConfig.getString("front_door_location.door_direction");
		
		House loadedHouse = new House(string, Bukkit.getWorld(houseConfig.getString("world")), firstLocation, secondLocation, frontDoorLocation, doorDirection);
		
		loadedHouse.setFile(file);
		loadedHouse.setConfiguration(configuration);
		loadedHouse.setHouseConfig(houseConfig);
		
		loadedHouse.addFixedMetadata();
	}
	
	public void addFixedMetadata()
	{	
		for(String blocks : houseConfig.getStringList("preset_blocks"))
		{
			String[] blockArray = blocks.split("@");
			Location location = new Location(world, Integer.valueOf(blockArray[0]), Integer.valueOf(blockArray[1]), Integer.valueOf(blockArray[2]));
			
			Block block = location.getBlock();
			block.setMetadata("PresetBlock", new FixedMetadataValue(plugin, true));
		}
	}
	
	public void saveHouseToFile(String houseName, Location firstPosition, Location secondPosition, Location frontDoorLocation, String doorDirection)
	{
		/*
		 * Move this to its own method for better reading of this Eventhandler.
		 */

		configuration.set(houseName + ".world", firstPosition.getWorld().getName());

		configuration.set(houseName + ".first_location.x", firstPosition.getBlockX());
		configuration.set(houseName + ".first_location.y", firstPosition.getBlockY());
		configuration.set(houseName + ".first_location.z", firstPosition.getBlockZ());

		configuration.set(houseName + ".second_location.x", secondPosition.getBlockX());
		configuration.set(houseName + ".second_location.y", secondPosition.getBlockY());
		configuration.set(houseName + ".second_location.z", secondPosition.getBlockZ());

		configuration.set(houseName + ".front_door_location.x", frontDoorLocation.getBlockX());
		configuration.set(houseName + ".front_door_location.y", frontDoorLocation.getBlockY());
		configuration.set(houseName + ".front_door_location.z", frontDoorLocation.getBlockZ());
		configuration.set(houseName + ".front_door_location.door_direction", doorDirection);

		try 
		{
			configuration.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void updateCurrentBlocks()
	{
		saveCurrentBlocks(world, minLocation, maxLocation, name);
		addFixedMetadata();
	}
	
	public void saveCurrentBlocks(World world, Location firstPosition, Location secondPosition, String houseName)
	{
		Vector min = Vector.getMinimum(firstPosition.toVector(), secondPosition.toVector());
		Vector max = Vector.getMaximum(firstPosition.toVector(), secondPosition.toVector());
		
		List<String> blockList = new ArrayList<String>();
		
		for(int i = min.getBlockX(); i <= max.getBlockX(); i++)
		{
			for(int j = min.getBlockY(); j <= max.getBlockY(); j++)
			{
				for(int k = min.getBlockZ(); k <= max.getBlockZ(); k++)
				{
					Block block = Bukkit.getServer().getWorld(world.getName()).getBlockAt(i, j, k);
					
					if(!block.getType().equals(Material.AIR))
					{
						blockList.add(block.getLocation().getBlockX() + "@" + block.getLocation().getBlockY() + "@" + block.getLocation().getBlockZ() + "@" + block.getType().toString());
					}
				}
			}
		}
		
		configuration.set(houseName + ".preset_blocks", blockList);
		
		try 
		{
			configuration.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
}
