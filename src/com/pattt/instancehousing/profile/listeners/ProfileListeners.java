package com.pattt.instancehousing.profile.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pattt.instancehousing.profile.Admin;
import com.pattt.instancehousing.profile.Profile;
import com.pattt.instancehousing.tools.MessageTool;

public class ProfileListeners implements Listener
{
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) 
	{
		long start = System.currentTimeMillis();
		Player player = event.getPlayer();


	
		if(player.hasPermission("admin"))
		{
			if(!Admin.getAdminProfiles().containsKey(player.getUniqueId()))
			{
				Admin.loadAdminProfile(player);

				long end = System.currentTimeMillis();
				player.sendMessage(MessageTool.color("&9Administrator profile for " + player.getName() + " was loaded successfully (" + (end - start) + "ms)."));	
			}
		}
		else
		{
			if(!Profile.getActiveProfiles().containsKey(player.getUniqueId()))
			{
				Profile.loadProfile(player);
				
				long end = System.currentTimeMillis();
				player.sendMessage(MessageTool.color("&9Profile " + player.getName() + " was loaded successfully (" + (end - start) + "ms)."));	
			}
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) 
	{
		Player player = event.getPlayer();

		Profile.unloadProfile(player);
	}
}
