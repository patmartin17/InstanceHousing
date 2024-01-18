package com.pattt.instancehousing.house.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.events.PlayerPurchaseHouseEvent;

public class HousePurchaseListener implements Listener
{	
	public static final InstanceHousing plugin = InstanceHousing.getInstance();
	
	@EventHandler
	public void onHousePurchase(PlayerPurchaseHouseEvent event)
	{
		
	}

}
