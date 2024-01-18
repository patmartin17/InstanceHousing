package com.pattt.instancehousing.house.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.events.OfferHouseEvent;
import com.pattt.instancehousing.house.managers.DoorManager;
import com.pattt.instancehousing.tools.DirectionTool;

public class OfferHouseListeners implements Listener
{
	private InstanceHousing plugin = InstanceHousing.getInstance();
	
	@EventHandler
	public void onOffer(OfferHouseEvent event)
	{
		Player player = event.getPlayer();	
		
		Bukkit.broadcastMessage("yo " + event.getPlayer().getName() + " you aint own this shit homie!");
		
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				DoorManager.closeDoor(event.getDoor(), player);
			}

		}.runTaskLater(plugin, 2L);	
		
		DirectionTool.teleportBackward(player);
	}
}
