package com.pattt.instancehousing.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class Admin extends Profile
{
	@Getter public static Map<UUID, Admin> adminProfiles = new HashMap<UUID, Admin>();
	//EITHER INSTATIATE THIS AUTOMATICALLY AND SAVE CONFIG OR CHANGE STRING TO HOUSE AND SAVE HOUSES.
	@Getter public static Map<Player, String> houseCreationMap = new HashMap<Player, String>();
	
	@Getter @Setter public boolean isInHouseCreation = false;
	@Getter @Setter public boolean isInAdminMode = false;
	
	public Admin(UUID uuid) 
	{
		super(uuid);
		
		adminProfiles.put(uuid, this);
	}
	
	public Admin(Player player)
	{
		this(player.getUniqueId());
	}
	
	public static Admin getExistingByUUID(UUID uuid)
	{
		if(adminProfiles.containsKey(uuid))
		{
			return adminProfiles.get(uuid);
		}
		return null;
	}
	
	public static Admin getByUUID(UUID uuid)
	{
		if(adminProfiles.containsKey(uuid))
		{
			return adminProfiles.get(uuid);
		}
		return null;
	}
	
	public static Admin getByPlayer(Player player)
	{
		
		if(adminProfiles.containsKey(player.getUniqueId()))
		{
			return adminProfiles.get(player.getUniqueId());
		}
		return null;
	}
	
	public Player getPlayer(Admin admin)
	{
		if(admin != null)
		{
			return Bukkit.getPlayer(admin.getUuid());	
		}
		return null;
	}
	
	public static void loadAdminProfile(Player player)
	{
		new Admin(player.getUniqueId());
	}
}
