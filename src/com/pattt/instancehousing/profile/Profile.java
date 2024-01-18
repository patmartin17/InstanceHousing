package com.pattt.instancehousing.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.tools.MessageTool;

import lombok.Getter;
import lombok.Setter;

public class Profile
{
	/*
	 * When moving to CGEssentials or whatever it will be called. Remove this and use the CGEssentials profile api.
	 */

	private static InstanceHousing plugin = InstanceHousing.getInstance();
	@Getter @Setter public UUID uuid;
	@Getter @Setter public Player player;
	@Getter @Setter public boolean loaded;

	// HOUSE THEY ARE CURRENTLY IN.
	@Getter protected static Map<UUID, Profile> activeProfiles = new HashMap<UUID, Profile>();
	@Getter @Setter public House house = null;
	@Getter @Setter public House isEnteringHouse = null;
	@Getter @Setter public House isExitingHouse = null;
	@Getter @Setter public List<PlayerHouse> ownedHouses;
	@Getter @Setter public boolean housesLoaded = false;
	@Getter @Setter public Player visiting;

	public Profile(UUID uuid)
	{
		setUuid(uuid);
		setPlayer(Bukkit.getPlayer(uuid));
		
		/*
		 * Determine if the player logged out inside their home.
		 */
		//setInHouse(true/false);
		
		activeProfiles.put(uuid, this);
		
		setLoaded(true);
	}
	
	/*
	 * Determines if the player has a house folder, gets the list of houses that the player owns,
	 * Loops through this keySet and creates a new player house that is loaded into the players
	 * Owned houses list. Finishes by setting houses as loaded for continuity.
	 */
	public static void loadPlayerHouses(Profile profile)
	{
		File houseFile = new File(plugin.getDataFolder() + "/players/" + profile.getUuid() + ".yml");

		if(!houseFile.exists())
			return;

		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(houseFile);

		ConfigurationSection playerHouses = configuration.getConfigurationSection("houses");

		if(playerHouses == null)
			return;

		List<PlayerHouse> ownedHouses;
		
		if(profile.getOwnedHouses() == null) 
		{
			ownedHouses = new ArrayList<PlayerHouse>();
		}
		else
		{
			ownedHouses = profile.getOwnedHouses();
		}

		for(String houses : playerHouses.getKeys(false))
		{
			//NEXT NEXT NEXT
			/*
			 * This wont work, add a string nameOfFurnitureObject or something better because it will empty the hashmap because they will all have the same profile. 
			 */
			Furniture furnitureObject = new Furniture(profile, House.getHouseByName(houses));

			PlayerHouse newPlayerHouse = new PlayerHouse(profile, House.getHouseByName(houses), furnitureObject);
			
			ownedHouses.add(newPlayerHouse);
		}	
		
		profile.setOwnedHouses(ownedHouses);
		
		profile.setHousesLoaded(true);
	}
	
	public void unloadPlayerHouses()
	{
		for(PlayerHouse housesToUnload : getOwnedHouses())
		{
			PlayerHouse.getLoadedPlayerHouses().remove(housesToUnload);
		}
		
		setOwnedHouses(null);
		
		setHousesLoaded(false);
	}
	
	public Profile(Player player)
	{
		this(player.getUniqueId());
	}

	public static Profile getExistingByUUID(UUID uuid)
	{
		if(activeProfiles.containsKey(uuid))
		{
			return activeProfiles.get(uuid);
		}
		return null;
	}

	public static Profile getByUUID(UUID uuid)
	{
		if(activeProfiles.containsKey(uuid))
		{
			return activeProfiles.get(uuid);
		}
		return new Profile(uuid);
	}

	public static Profile getByPlayer(Player player)
	{

		if(activeProfiles.containsKey(player.getUniqueId()))
		{
			return activeProfiles.get(player.getUniqueId());
		}
		return null;
	}

	public static Set<Profile> getProfiles()
	{
		return new HashSet<Profile>(activeProfiles.values());
	}


	public static void loadProfile(Player player)
	{
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if(profile == null)
		{
			Bukkit.getConsoleSender().sendMessage(MessageTool.color(player.getName() + "&c's profile was not loaded correctly"));
		}
	}

	public static void unloadProfile(Player player)
	{
		Profile profile = Profile.getExistingByUUID(player.getUniqueId());

		if (profile != null) 
		{
			Profile.getProfiles().remove(profile);
			return;
		} 
		Bukkit.getConsoleSender().sendMessage(MessageTool.color("&c" + player.getName() + "'s profile was not correctly saved."));
	}
}
