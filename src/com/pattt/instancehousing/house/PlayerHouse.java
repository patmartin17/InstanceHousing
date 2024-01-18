package com.pattt.instancehousing.house;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.profile.Profile;

import lombok.Getter;
import lombok.Setter;

public class PlayerHouse 
{
	@Getter protected static Map<PlayerHouse, Profile> loadedPlayerHouses = new HashMap<PlayerHouse, Profile>();
	@Getter public static final InstanceHousing plugin = InstanceHousing.getInstance();
	@Getter @Setter public Profile profile;
	@Getter @Setter public Furniture furniture;
	@Getter @Setter public House house;
	@Getter @Setter public boolean isFurnitureLoaded;
	@Getter @Setter public Player visitor;

	public PlayerHouse(Profile profile, House house, Furniture furniture)
	{
		setProfile(profile);
		setHouse(house);
		setFurniture(furniture);

		loadedPlayerHouses.put(this, profile);

	}

	/*
	 * Loops through the list of loaded player houses, first finds if the specified house is in the list,
	 * then looks for if that house is owned by the correct player, then returns that house that met both
	 * expectations.
	 */
	public static PlayerHouse getPlayerHouse(Profile profile, House house)
	{
		for(Entry<PlayerHouse, Profile> profiles : loadedPlayerHouses.entrySet())
		{
			if(profiles.getKey().getHouse().equals(house) && profiles.getValue().equals(profile))
			{
				return profiles.getKey();
			}
		}
		return null;
	}

	public static void loadNewPlayerHouse(Profile profile, House house)
	{
		List<PlayerHouse> ownedHouses;
		
		if(profile.getOwnedHouses() == null) 
		{
			ownedHouses = new ArrayList<PlayerHouse>();
		}
		else
		{
			ownedHouses = profile.getOwnedHouses();
		}
		
		Furniture furnitureObject = new Furniture(profile, house);

		PlayerHouse newPlayerHouse = new PlayerHouse(profile, house, furnitureObject);	
		
		ownedHouses.add(newPlayerHouse);

		profile.setOwnedHouses(ownedHouses);

		loadNewPlayerHouseIntoFile(profile, house);
	}

	private static void loadNewPlayerHouseIntoFile(Profile profile, House house)
	{
		File file = new File(plugin.getDataFolder(), "/players/" + profile.getUuid() + ".yml");

		YamlConfiguration useFile = YamlConfiguration.loadConfiguration(file);

		List<String> emptyFurniture = new ArrayList<String>();

		useFile.set("houses." + house.getName(), emptyFurniture);	

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
