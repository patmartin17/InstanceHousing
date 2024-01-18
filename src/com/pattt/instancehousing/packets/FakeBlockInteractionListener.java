package com.pattt.instancehousing.packets;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.events.PlayerInteractFakeBlockEvent;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.blocks.BlockHandler;
import com.pattt.instancehousing.packets.wrappers.WrapperPlayServerBlockChange;
import com.pattt.instancehousing.profile.Profile;

public class FakeBlockInteractionListener implements Listener
{
	private static InstanceHousing plugin = InstanceHousing.getInstance();
	private static BlockHandler handler = plugin.getBlockHandler();
	
	public void protectFakeBlocks() 
	{
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE) 
		{
			@Override
			public void onPacketSending(PacketEvent event) 
			{
				WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event.getPacket());

				Player player = event.getPlayer();
				Profile profile = Profile.getExistingByUUID(player.getUniqueId());
				
				if(profile == null)
					return;

				if(profile.getHouse() == null)
					return;
				
				House house = profile.getHouse();
				PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, house);
				
				if(playerHouse.isFurnitureLoaded())
				{
					Location blockLocation = new Location(player.getWorld(), packet.getLocation().getX(), packet.getLocation().getY(), packet.getLocation().getZ());

					if(handler.getPlacingBlockMap().contains(player))
					{
						event.setCancelled(true);	
					}
					
					if(playerHouse.getFurniture().getFurnitureList().containsKey(blockLocation))
					{
						if(!playerHouse.getFurniture().getReloadingBlocks().contains(blockLocation))
						{
							event.setCancelled(true);		
						}
					}
				}
			}
		});
	}

	
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		
		Block block = event.getClickedBlock();
		House house = profile.getHouse();
		
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;
		
		if(profile.getHouse() == null)
			return;
		
		PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, house);
		
		if(playerHouse.isFurnitureLoaded())
		{
			Location blockLocation = new Location(player.getWorld(), block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ());
			
			if(playerHouse.getFurniture().getFurnitureList().containsKey(blockLocation))
			{
				Bukkit.getPluginManager().callEvent(new PlayerInteractFakeBlockEvent(player, playerHouse.getFurniture().getFurnitureList().get(blockLocation), blockLocation));
			}
		}
		
	}
}
