package com.pattt.instancehousing.tester;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.events.PlayerEnterHouseEvent;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.profile.Profile;

public class LagTester implements Listener
{
	private InstanceHousing plugin = InstanceHousing.getInstance();
	
	@EventHandler
	public void onEnterHouse(PlayerEnterHouseEvent event)
	{
		Player player = event.getPlayer();
		
		Profile profile = Profile.getByPlayer(player);
		
		House house = profile.getHouse();
	
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				Furniture furniture = new Furniture(profile, house);
				
				Bukkit.broadcastMessage(furniture.toString());
				
				Bukkit.broadcastMessage(Runtime.getRuntime().freeMemory() + "");
			}
		}.runTaskTimer(plugin, 60L, 60L);
	}
}
