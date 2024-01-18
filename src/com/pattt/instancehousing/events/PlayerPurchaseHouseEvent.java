package com.pattt.instancehousing.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pattt.instancehousing.house.House;

import lombok.Getter;
import lombok.Setter;

public final class PlayerPurchaseHouseEvent extends Event 
{
	@Getter @Setter private Player player;
	@Getter @Setter private House house;

	private static final HandlerList HANDLERS = new HandlerList();

	public PlayerPurchaseHouseEvent(Player player, House house) 
	{
		setPlayer(player);
		setHouse(house);
	}

	public HandlerList getHandlers() 
	{
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() 
	{
		return HANDLERS;
	}
}
