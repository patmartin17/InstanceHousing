package com.pattt.instancehousing.packets;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.blocks.DividableBlock;
import com.pattt.instancehousing.packets.wrappers.WrapperPlayClientBlockDig;
import com.pattt.instancehousing.profile.Profile;

public class BlockBreakPacketListener implements Listener
{
	private InstanceHousing plugin = InstanceHousing.getInstance();
	
	public void handleBlockBreaking() 
	{
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) 
		{
			@Override
			public void onPacketReceiving(PacketEvent event) 
			{
				WrapperPlayClientBlockDig packet = new WrapperPlayClientBlockDig(event.getPacket());
				Player player = event.getPlayer();
				Profile profile = Profile.getExistingByUUID(player.getUniqueId());
				
				if(profile == null)
					return;
				
				if(profile.getHouse() == null)
					return;
				
				House house = profile.getHouse();
				PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, house);

				Location blockLocation = new Location(player.getWorld(), packet.getLocation().getX(), packet.getLocation().getY(), packet.getLocation().getZ());
				
				if(playerHouse.getFurniture().getFurnitureList().containsKey(blockLocation))
				{
					if(playerHouse.isFurnitureLoaded())
					{
						if(packet.getStatus().toString().equals("STOP_DESTROY_BLOCK") || (player.getGameMode().equals(GameMode.CREATIVE) && packet.getStatus().toString().equals("START_DESTROY_BLOCK")))
						{
							BlockData blockData = playerHouse.getFurniture().getFurnitureList().get(blockLocation);

							new BukkitRunnable() 
							{
								@Override
								public void run() 
								{
									if(isDividableBlock(player, playerHouse, blockLocation, blockData))
										return;
									
									/*
									 * Change this to a method that drops the right block drop. And create a method that adds the drops to an arraylist
									 * 
									 * that will be hidden to newly joined players.
									 */
									
									if(player.getGameMode().equals(GameMode.CREATIVE))
										return;
									
									Location centerLocation = new Location(player.getWorld(), blockLocation.getBlockX() + 0.5D, blockLocation.getBlockY(), blockLocation.getBlockZ() + 0.5D);
									
									Item droppedItem = player.getWorld().dropItem(centerLocation, new ItemStack(blockData.getMaterial()));
									
									droppedItem.setVelocity(new Vector(0, 0, 0));
									droppedItem.setGravity(false);
									
									droppedItem.setMetadata("HousingDrop", new FixedMetadataValue(plugin, player.getName()));
									
									for(Player players : Bukkit.getOnlinePlayers())
									{
										if(players != player)
										{
											players.hideEntity(plugin, droppedItem);		
										}
									}
								
								}
							}.runTaskLater(plugin, 1L);

						}
					}
				}
			}
		});
	}
	
	public boolean isDividableBlock(Player player, PlayerHouse playerHouse, Location location, BlockData blockData) 
	{	
		for(DividableBlock blocks : DividableBlock.values())
		{
			if(blocks.toString().equals(blockData.getMaterial().name().toUpperCase()))
			{
				DividableBlock dividableBlock = DividableBlock.valueOf(blockData.getMaterial().name().toUpperCase());
				
				dividableBlock.handleInteraction(player, playerHouse, location, blockData);
				return true;
			}		
		}
		return false;
	}

}
