package com.pattt.instancehousing.events;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

public final class PlayerInteractFakeBlockEvent extends Event 
{
	@Getter @Setter private Player player;
	@Getter @Setter private BlockData blockData;
	@Getter @Setter private Location location;

	private static final HandlerList HANDLERS = new HandlerList();

	public PlayerInteractFakeBlockEvent(Player player, BlockData blockData, Location location) 
	{
		setPlayer(player);
		setBlockData(blockData);
		setLocation(location);
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
