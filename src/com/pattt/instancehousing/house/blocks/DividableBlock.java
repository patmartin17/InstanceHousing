package com.pattt.instancehousing.house.blocks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.managers.BlockManager;

public enum DividableBlock
{
	CHEST 
	{

		@SuppressWarnings({ "unchecked" })
		@Override
		public void handleInteraction(Player player, PlayerHouse playerHouse, Location location, BlockData blockData) 
		{
			/*
			 * What happens when they delete a chest, add another chest to make a double chest, remove half a chest. If you remove half a chest the contents will be dropped and 
			 * 
			 * that chest will be set back to a single chest. If you add a chest then it will change the config to a double chest config. 
			 */

			File file = new File(plugin.getDataFolder() + "/storage/" + player.getUniqueId() + ".yml");
			YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
			ConfigurationSection chestsConfig = configuration.getConfigurationSection(playerHouse.getHouse().getName() + ".chests");

			List<ItemStack> chestContents = null;

			Location centerLocation = new Location(player.getWorld(), location.getBlockX() + 0.5D, location.getBlockY(), location.getBlockZ() + 0.5D);

			if(!blockData.getAsString().contains("single"))
			{
				String[] blockDataSplit = blockData.getAsString().split(",");
				String[] directionData = blockDataSplit[0].split("=");
				String[] sideData = blockDataSplit[1].split("=");

				Location otherHalfLocation = InteractableBlock.determineChestHalves(directionData[1], sideData[1], player, location);

				House house = playerHouse.getHouse();
				Furniture furniture = playerHouse.getFurniture();

				furniture.getFurnitureList().remove(location);
				furniture.getFurnitureList().remove(otherHalfLocation);

				String newBlockData = "minecraft:chest[facing=" + directionData[1] + ",type=single,waterlogged=false]";

				furniture.getFurnitureList().put(otherHalfLocation, Bukkit.createBlockData(newBlockData));

				BlockManager.sendSingleBlockChange(player, otherHalfLocation, Bukkit.createBlockData(newBlockData));

				List<ItemStack> otherHalfContents = new ArrayList<ItemStack>();

				String storageEntry = null;

				if(sideData[1].equals("left"))
				{
					storageEntry = otherHalfLocation.getBlockX() + "," + otherHalfLocation.getBlockY() + "," 
							+ otherHalfLocation.getBlockZ() + "@" + location.getBlockX()  + "," + location.getBlockY() + "," + location.getBlockZ();
					if(chestsConfig.contains(storageEntry))
					{ 
						chestContents = (List<ItemStack>) configuration.getList(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents");
						ItemStack[] contents = chestContents.toArray(new ItemStack[0]);

						for(int i = 0; i < 27; i++)
						{
							otherHalfContents.add(contents[i]);
						}

						for(int i = 27; i < 54; i++)
						{
							ItemStack item = contents[i];
							if(item != null)
							{
								Item droppedItem = player.getWorld().dropItem(centerLocation, item);

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
						}
					}
				}
				else
				{
					storageEntry = location.getBlockX()  + "," + location.getBlockY() + "," + location.getBlockZ() 
					+ "@" + otherHalfLocation.getBlockX() + "," + otherHalfLocation.getBlockY() + "," + otherHalfLocation.getBlockZ();

					if(chestsConfig.contains(storageEntry))
					{
						chestContents = (List<ItemStack>) configuration.getList(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents");
						ItemStack[] contents = chestContents.toArray(new ItemStack[0]);

						for(int i = 27; i < 54; i++)
						{
							otherHalfContents.add(contents[i]);
						}

						for(int i = 0; i < 27; i++)
						{
							ItemStack item = contents[i];
							if(item != null)
							{
								Item droppedItem = player.getWorld().dropItem(centerLocation, item);

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
						}
					}
				}

				configuration.set(playerHouse.getHouse().getName() + ".chests." + storageEntry, null);

				try 
				{
					configuration.save(file);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}	

				ItemStack[] newItems = otherHalfContents.toArray(new ItemStack[0]);

				InteractableBlock.saveChestData(player, house, otherHalfLocation, null, newItems);


				//remove both sides from furniture list. Add this one back as a chest.
				//Change the block in game with a reload block. 
				//remove the chest entry
				//in the config with both locations and the @ in the middle. Save the contents of the chest before deleting it in the config 
				//and make an itemStack[] for both sides of chest
				//and drop the contents on the floor
				// of the previous chest that was broken by checking items 27-53 and dropping them. 
			}
			else
			{
				String storageEntry = location.getBlockX()  + "," + location.getBlockY() + "," + location.getBlockZ();
				if(chestsConfig.contains(storageEntry))
				{
					chestContents = (List<ItemStack>) configuration.getList(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents");
					ItemStack[] contents = chestContents.toArray(new ItemStack[0]);

					for(ItemStack item : contents)
					{
						if(item != null)
						{
							Item droppedItem = player.getWorld().dropItem(centerLocation, item);

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
					}

					configuration.set(playerHouse.getHouse().getName() + ".chests." + storageEntry, null);
					try 
					{
						configuration.save(file);
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}

	};

	private static InstanceHousing plugin = InstanceHousing.getInstance();

	public abstract void handleInteraction(Player player, PlayerHouse playerHouse, Location location, BlockData blockData);

}
