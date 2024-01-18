package com.pattt.instancehousing.house.entities;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.profile.Profile;

@SuppressWarnings("deprecation")
public class FakeEntityListeners implements Listener
{
	private static InstanceHousing plugin = InstanceHousing.getInstance();

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();

		Profile profile = Profile.getByPlayer(player);

		if(event.getItem().hasMetadata("HousingDrop"))
		{
			List<MetadataValue> values = event.getItem().getMetadata("HousingDrop");
			if(profile.getHouse() != null)
			{
				if(!values.isEmpty() && !values.get(0).asString().equals(player.getName()))
				{
					event.setCancelled(true);
				}
			}
			else
			{
				event.setCancelled(true);
			}
		}

	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();

		Profile profile = Profile.getByPlayer(player);

		if(profile.getHouse() != null)
		{
			Item droppedItem = event.getItemDrop();

			droppedItem.setMetadata("HousingDrop", new FixedMetadataValue(plugin, player.getName()));

			for(Player players : Bukkit.getOnlinePlayers())
			{
				if(players != player)
				{
					players.hideEntity(plugin, droppedItem);		
				}
			}
		}
	}

	//@EventHandler
	//public void onArrowPickup(PlayerPickupArrowEvent event)
	//{

	//}
}
