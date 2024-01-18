package com.pattt.instancehousing.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TestListener implements Listener
{
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{	
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			Block block = event.getClickedBlock();
			if(block.hasMetadata("PresetBlock"))
			{
				Bukkit.broadcastMessage("true");
			}
			else
			{
				Bukkit.broadcastMessage("false");
			}
		}
	
	}
}
