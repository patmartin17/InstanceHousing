package com.pattt.instancehousing.house.blocks;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.profile.Profile;

import lombok.Getter;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.block.Block;

public enum InteractableBlock
{
	CHEST {
		@SuppressWarnings("unchecked")
		public void handleInteraction(Player player, Location location) 
		{
			Profile profile = Profile.getByPlayer(player);
			House house = profile.getHouse();
			PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, house);
			Furniture furniture = playerHouse.getFurniture();

			File file = new File(plugin.getDataFolder() + "/storage/" + player.getUniqueId() + ".yml");
			YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

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

			PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(position, chest, 1, 54);
			PlayerConnection connection = (PlayerConnection) craftPlayer.getHandle().c;
			connection.a(packet);

			player.playSound(location, Sound.BLOCK_CHEST_OPEN, 0.5F, 1);

			BlockData blockData = furniture.getFurnitureList().get(location);

			String storageEntry = null;
			List<ItemStack> chestContents = null;

			if(blockData.getAsString().contains("single"))
			{
				Inventory inventory = Bukkit.createInventory(null, 27);

				storageEntry = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();

				if(configuration.get(playerHouse.getHouse().getName() + ".chests." + storageEntry) == null)
				{
					saveChestData(player, house, location, null, inventory.getContents());	
				}
				else
				{
					chestContents = (List<ItemStack>) configuration.getList(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents");
					ItemStack[] contents = chestContents.toArray(new ItemStack[0]);

					for(int i = 0; i < 27; i++)
					{
						if(contents[i] != null)
						{
							inventory.setItem(i, contents[i]);
						}
					}
				}
				inFakeBlockInventory.put(player, location);
				player.openInventory(inventory);
			}
			else	
			{
				String[] blockDataSplit = blockData.getAsString().split(",");
				String[] directionData = blockDataSplit[0].split("=");
				String[] sideData = blockDataSplit[1].split("=");

				Location otherHalfLocation = determineChestHalves(directionData[1], sideData[1], player, location);

				Inventory inventory = Bukkit.createInventory(null, 54);

				if(sideData[1].equals("left"))
				{
					storageEntry = location.getBlockX()  + "," + location.getBlockY() + "," + location.getBlockZ() 
					+ "@" + otherHalfLocation.getBlockX() + "," + otherHalfLocation.getBlockY() + "," + otherHalfLocation.getBlockZ();
					
					if(configuration.get(playerHouse.getHouse().getName() + ".chests." + storageEntry) == null)
					{
						saveChestData(player, house, location, otherHalfLocation, inventory.getContents());		
					}
					else
					{
						chestContents = (List<ItemStack>) configuration.getList(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents");
						ItemStack[] contents = chestContents.toArray(new ItemStack[0]);

						for(int i = 0; i < 54; i++)
						{
							if(contents[i] != null)
							{
								inventory.setItem(i, contents[i]);
							}
						}		
					}		

				}
				else
				{
					storageEntry = otherHalfLocation.getBlockX() + "," + otherHalfLocation.getBlockY() + "," 
					+ otherHalfLocation.getBlockZ() + "@" + location.getBlockX()  + "," + location.getBlockY() + "," + location.getBlockZ();
					
					if(configuration.get(playerHouse.getHouse().getName() + ".chests." + storageEntry) == null)
					{
						saveChestData(player, house, otherHalfLocation, location, inventory.getContents());	
					}
					else
					{
						chestContents = (List<ItemStack>) configuration.getList(playerHouse.getHouse().getName() + ".chests." + storageEntry + ".contents");
						ItemStack[] contents = chestContents.toArray(new ItemStack[0]);

						for(int i = 0; i < 54; i++)
						{
							if(contents[i] != null)
							{
								inventory.setItem(i, contents[i]);
							}
						}			
					}
				}

				inFakeBlockInventory.put(player, location);
				player.openInventory(inventory);
			}

			/*
			 * What happens when they delete a chest, add another chest to make a double chest, remove half a chest. If you remove half a chest the contents will be dropped and 
			 * 
			 * that chest will be set back to a single chest. If you add a chest then it will change the config to a double chest config. 
			 */
		}
	},

	CRAFTING_TABLE {
		public void handleInteraction(Player player, Location location) 
		{
			player.openWorkbench(location, true);
			inFakeBlockInventory.put(player, location);
		}
	},

	LOOM {
		public void handleInteraction(Player player, Location location) 
		{
			player.openInventory(Bukkit.createInventory(player, InventoryType.LOOM));	
			inFakeBlockInventory.put(player, location);
		}
	},

	FURNACE {
		public void handleInteraction(Player player, Location location)
		{
			player.openInventory(Bukkit.createInventory(player, InventoryType.FURNACE));
			inFakeBlockInventory.put(player, location);
		}
	};

	@Getter public static final InstanceHousing plugin = InstanceHousing.getInstance();

	public static final Map<Player, Location> inFakeBlockInventory = new HashMap<Player, Location>();

	public abstract void handleInteraction(Player player, Location location);

	public static Location determineChestHalves(String direction, String currentHalf, Player player, Location location)
	{
		switch (direction)
		{
		case "north": 
		{
			if(currentHalf.equals("right"))
			{
				return new Location(player.getWorld(), location.getX() - 1, location.getY(), location.getZ());
			}
			else if(currentHalf.equals("left"))
			{
				return new Location(player.getWorld(), location.getX() + 1, location.getY(), location.getZ());
			}
			return null;
		}
		case "south": 
		{	
			if(currentHalf.equals("right"))
			{
				return new Location(player.getWorld(), location.getX() + 1, location.getY(), location.getZ());
			}
			else if(currentHalf.equals("left"))
			{
				return new Location(player.getWorld(), location.getX() - 1, location.getY(), location.getZ());
			}
			return null;
		}
		case "east": 
		{
			if(currentHalf.equals("right"))
			{
				return new Location(player.getWorld(), location.getX(), location.getY(), location.getZ() - 1);
			}
			else if(currentHalf.equals("left"))
			{
				return new Location(player.getWorld(), location.getX(), location.getY(), location.getZ() + 1);
			}
			return null;
		}
		case "west": 
		{
			if(currentHalf.equals("right"))
			{
				return new Location(player.getWorld(), location.getX(), location.getY(), location.getZ() + 1);
			}
			else if(currentHalf.equals("left"))
			{
				return new Location(player.getWorld(), location.getX(), location.getY(), location.getZ() - 1);
			}
			return null;
		}
		default: return null;
		}
	}

	public static void saveChestData(Player player, House house, Location blockLocationOne, Location blockLocationTwo, ItemStack[] contents)
	{
		Profile profile = Profile.getByPlayer(player);

		File file = new File(plugin.getDataFolder(), "/storage/" + profile.getUuid() + ".yml");

		YamlConfiguration useFile = YamlConfiguration.loadConfiguration(file);

		String blockLocation = null;

		if(blockLocationTwo != null)
		{
			blockLocation = blockLocationOne.getBlockX() + "," + blockLocationOne.getBlockY() + "," + blockLocationOne.getBlockZ() 
			+ "@" + blockLocationTwo.getBlockX() + "," + blockLocationTwo.getBlockY() + "," + blockLocationTwo.getBlockZ();		
			useFile.set(house.getName() + ".chests." + blockLocation + ".type", "double");
			useFile.set(house.getName() + ".chests." + blockLocation + ".left", blockLocationOne);
			useFile.set(house.getName() + ".chests." + blockLocation + ".right", blockLocationTwo);
		}
		else
		{
			blockLocation = blockLocationOne.getBlockX() + "," + blockLocationOne.getBlockY() + "," + blockLocationOne.getBlockZ();
			useFile.set(house.getName() + ".chests." + blockLocation + ".type", "single");
			useFile.set(house.getName() + ".chests." + blockLocation + ".location", blockLocationOne);
		}

		useFile.set(house.getName() + ".chests." + blockLocation + ".contents", contents);

		try 
		{
			useFile.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	
	}

	//https://stackoverflow.com/questions/42264195/refactoring-code-with-too-many-switch-cases
}
