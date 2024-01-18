package com.pattt.instancehousing.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.pattt.instancehousing.house.House;

import lombok.Getter;
import lombok.Setter;

public final class OfferHouseEvent extends Event 
{
	@Getter @Setter private Player player;
	@Getter @Setter private House house;
	@Getter @Setter private Block door;

	private static final HandlerList HANDLERS = new HandlerList();

	public OfferHouseEvent(Player player, Block door, House house) 
	{
		setPlayer(player);
		setHouse(house);
		setDoor(door);;
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
