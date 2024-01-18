package com.pattt.instancehousing.house.listeners;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.events.PlayerInteractFakeBlockEvent;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.blocks.InteractableBlock;
import com.pattt.instancehousing.profile.Profile;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.block.Block;

public class FurnitureListeners implements Listener
{
	private static InstanceHousing plugin = InstanceHousing.getInstance();

	@EventHandler
	public void onFurnitureInteract(PlayerInteractFakeBlockEvent event)
	{
		Player player = event.getPlayer();
		Location location = event.getLocation();
		BlockData data = event.getBlockData();

		refreshBlock(player, location, data);

		String blockName = data.getMaterial().name().toUpperCase();

		if(player.isSneaking())
			return;

		for(InteractableBlock blocks : InteractableBlock.values())
		{
			if(blocks.toString().contains(blockName))
			{
				InteractableBlock interactableBlock = InteractableBlock.valueOf(blockName);

				interactableBlock.handleInteraction(player, location);
			}		
		}

	}

	@SuppressWarnings({"unchecked"})
	@EventHandler
	public void onChestClose(InventoryCloseEvent event)
	{
		Player player = (Player) event.getPlayer();
		Profile profile = Profile.getByPlayer(player);

		if(profile.getHouse() == null)
			return;
		
		PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, profile.getHouse());
		Furniture furniture = playerHouse.getFurniture();
		
		ItemStack[] beginningContents = null;
		ItemStack[] endingContents = event.getInventory().getContents();

		File file = new File(plugin.getDataFolder() + "/storage/" + player.getUniqueId() + ".yml");
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		List<ItemStack> chestContents = null;
		
		if(InteractableBlock.inFakeBlockInventory.containsKey(player))
		{
			Location location = InteractableBlock.inFakeBlockInventory.get(player);
			BlockData blockData = furniture.getFurnitureList().get(location);

			if(furniture.getFurnitureList().containsKey(location))
			{
				CraftPlayer craftPlayer = (CraftPlayer) player;
				BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

				Method method;
				Block chest = null;

				try 
				{
					method = CraftBlock.class.getDeclaredMethod("getNMSBlock");
					method.setAccessible(true);
					chest = (Block) method.invoke(location.getBlock());
				} 
				catch (Exception ex){}

				PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(position, chest, 1, 0);
				PlayerConnection connection = craftPlayer.getHandle().c;
				connection.a(packet);

				player.playSound(location, Sound.BLOCK_CHEST_CLOSE, 0.5F, 1);

				String storageEntry = null;

				if(!blockData.getAsString().contains("single"))
				{
					String[] blockDataSplit = blockData.getAsString().split(",");
					String[] directionData = blockDataSplit[0].split("=");
					String[] sideData = blockDataSplit[1].split("=");

					Location otherHalfLocation = InteractableBlock.determineChestHalves(directionData[1], sideData[1], player, location);

					if(sideData[1].equals("left"))
					{
						storageEntry = location.getBlockX()  + "," + location.getBlockY() + "," + location.getBlockZ() 
						+ "@" + otherHalfLocation.getBlockX() + "," + otherHalfLocation.getBlockY() + "," + otherHalfLocation.getBlockZ();

						chestContents = (List<ItemStack>) configuration.getList(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents");
						beginningContents = chestContents.toArray(new ItemStack[0]);

						if(beginningContents != endingContents)
						{
							configuration.set(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents", endingContents);
							try 
							{
								configuration.save(file);
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							}
						}
						InteractableBlock.inFakeBlockInventory.remove(player);
					}
					else
					{
						storageEntry = otherHalfLocation.getBlockX() + "," + otherHalfLocation.getBlockY() + "," 
								+ otherHalfLocation.getBlockZ() + "@" + location.getBlockX()  + "," + location.getBlockY() + "," + location.getBlockZ();

						chestContents = (List<ItemStack>) configuration.getList(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents");
						beginningContents = chestContents.toArray(new ItemStack[0]);

						if(beginningContents != endingContents)
						{
							configuration.set(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents", endingContents);
							try 
							{
								configuration.save(file);
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							}
						}
						InteractableBlock.inFakeBlockInventory.remove(player);
					}
				}
				else
				{
					storageEntry = location.getBlockX()  + "," + location.getBlockY() + "," + location.getBlockZ();
					chestContents = (List<ItemStack>) configuration.getList(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents");
					beginningContents = chestContents.toArray(new ItemStack[0]);

					if(beginningContents != endingContents)
					{
						configuration.set(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents", endingContents);
						try 
						{
							configuration.save(file);
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
					}
					InteractableBlock.inFakeBlockInventory.remove(player);
				}
			}
		}
	}

	/*
	 * Sends a quick block change refresh with BlockData.
	 */
	private void refreshBlock(Player player, Location location, BlockData blockData) 
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				player.sendBlockChange(location, blockData);
			}
		}.runTaskLater(plugin, 1L);
	}
}
