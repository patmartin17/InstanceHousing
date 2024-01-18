package com.pattt.instancehousing.house;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.profile.Profile;

import lombok.Getter;
import lombok.Setter;

public class Furniture 
{

	private static InstanceHousing plugin = InstanceHousing.getInstance();
	@Getter @Setter public House house;
	/*
	 * We will be switching to using an 3DArray instead of a HashMap.
	 */
	@Getter @Setter public Map<Location, BlockData> furnitureList;
	@Getter @Setter public Profile profile;
	@Getter @Setter public int furnitureAmount;
	@Getter @Setter public List<Location> reloadingBlocks = new ArrayList<Location>();

	public Furniture(Profile profile, House house)
	{
		setHouse(house);
		setProfile(profile);

		loadFurniture();
	}

	/*
	 * Splits serialized strings that were saved into player data files and places the block data into the map. 
	 * 
	 * NOTE: This specified map will be changed to 3D Array. 
	 */
	private void loadFurniture()
	{
		File file = new File(plugin.getDataFolder() + "/players/" + profile.getPlayer().getUniqueId() + ".yml");

		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		
		ArrayList<String> listOfBlocks = (ArrayList<String>) configuration.getStringList("houses." + house.getName() + ".furniture");

		furnitureList = new HashMap<Location, BlockData>();
		
		for(String string : listOfBlocks)
		{

			String[] cutString = string.split("@");

			BlockData data = Bukkit.createBlockData(cutString[0]);
			
			Location location = new Location(profile.getPlayer().getWorld(), Integer.valueOf(cutString[1]), Integer.valueOf(cutString[2]), Integer.valueOf(cutString[3]));
			
			furnitureList.put(location, data);
			
		}
		
		furnitureAmount = furnitureList.size();
	}
	
	/*
	 * Uses the saveNewFurnature() method to update any newly placed furnature 
	 */
	public static void updateFurniture(Player player)
	{
		Profile profile = Profile.getByPlayer(player);
		if(profile.getHouse() != null)
		{
			PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, profile.getHouse());
			Furniture.saveNewFurniture(playerHouse.getFurniture());
		}
	}
	
	public static void saveNewFurniture(Furniture furniture)
	{		
	    File file = new File(plugin.getDataFolder(), "/players/" + furniture.getProfile().getPlayer().getUniqueId() + ".yml");
	    
	    if(!file.exists())
	    	return;
	    
	    YamlConfiguration useFile = YamlConfiguration.loadConfiguration(file);
	    
	    List<String> temporaryList = new ArrayList<String>();
	    
	    for(Location location : furniture.getFurnitureList().keySet())
	    {
	    	String serializedFurniture = furniture.getFurnitureList().get(location).getAsString() 
	    	+ "@" + location.getBlockX() 
	    	+ "@" + location.getBlockY() 
	    	+ "@" + location.getBlockZ();
	    	temporaryList.add(serializedFurniture);
	    }
	    
	    useFile.set("houses." + furniture.getHouse().getName() + ".furniture", temporaryList);
	    
		try 
		{
			useFile.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
