package com.pattt.instancehousing;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.house.blocks.BlockHandler;
import com.pattt.instancehousing.house.creation.HouseCreationCommands;
import com.pattt.instancehousing.house.creation.HouseCreationListeners;
import com.pattt.instancehousing.house.entities.FakeEntityListeners;
import com.pattt.instancehousing.house.listeners.FrontDoorListener;
import com.pattt.instancehousing.house.listeners.FurnitureListeners;
import com.pattt.instancehousing.house.listeners.HouseEntranceListener;
import com.pattt.instancehousing.house.listeners.HouseExitListener;
import com.pattt.instancehousing.house.listeners.HouseListeners;
import com.pattt.instancehousing.house.listeners.OfferHouseListeners;
import com.pattt.instancehousing.listeners.PlayerJoinListeners;
import com.pattt.instancehousing.listeners.PlayerQuitListeners;
import com.pattt.instancehousing.packets.BlockBreakPacketListener;
import com.pattt.instancehousing.packets.FakeBlockInteractionListener;
import com.pattt.instancehousing.profile.Admin;
import com.pattt.instancehousing.profile.Profile;
import com.pattt.instancehousing.profile.listeners.ProfileListeners;
import com.pattt.instancehousing.tools.MessageTool;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;

public class InstanceHousing extends JavaPlugin
{
	@Getter public static InstanceHousing instance;
	
	@Getter public BlockHandler blockHandler;

	@Getter private PaperCommandManager commandManager;

	@Getter private ProtocolManager protocolManager;
	
	@Override
	public void onEnable()
	{
		/*
		 * Instantiate variables and plugin instance.
		 */
		instance = this;
		PluginManager pluginManager = Bukkit.getPluginManager();
		protocolManager = ProtocolLibrary.getProtocolManager();
		blockHandler = new BlockHandler();
		
		/*
		 * Uses the blockHandler object to load all block managers into active objects. 
		 */
		blockHandler.loadBlockManagers();
		
		/*
		 * Executes registry methods for commands, listeners, profiles, and available managers. 
		 */
		registerListeners(pluginManager);
		registerProfileListeners(pluginManager);
		registerManagers();
		registerCommands();

		/*
		 * Registers any packet listeners that handle fake block packets. 
		 */
		registerPacketListeners();
		
		/*
		 * Loads house objects first in order for PlayerHouse and Profile to work correctly. 
		 */
		loadHouses();
		
		/*
		 * Loads all player profiles that are currently online when onEnable runs. 
		 */
		loadProfiles();
		
		/*
		 * Loads each player house based on all current loaded profiles. 
		 */
		loadPlayerHouses();

		/*
		 * Saves default config and reloads to use new data | Replacing this with our own Configuration later. 
		 */
		saveDefaultConfig();
		reloadConfig();
	}

	@Override
	public void onDisable()
	{
		/*
		 * Updates and saves player housing that may have changed after last reload. 
		 */
		updatePlayerFurniture();
	}

	/*
	 * Register any miscellanous listeners as well as calls any other register listener method (Main listener registrator)
	 */
	private void registerListeners(PluginManager pluginManager)
	{	
		registerProfileListeners(pluginManager);
		registerHouseListeners(pluginManager);
		
		pluginManager.registerEvents(new FakeBlockInteractionListener(), this);
		pluginManager.registerEvents(new BlockBreakPacketListener(), this);
		pluginManager.registerEvents(new FakeEntityListeners(), this);
		//pluginManager.registerEvents(new LagTester(), this);
	}
	
	/*
	 * Register packets for housing blocks.
	 */
	private void registerPacketListeners()
	{
		FakeBlockInteractionListener fakeBlockInteractionListener = new FakeBlockInteractionListener();
		fakeBlockInteractionListener.protectFakeBlocks();
		
		BlockBreakPacketListener blockBreakPacketListener = new BlockBreakPacketListener();
		blockBreakPacketListener.handleBlockBreaking();
	}

	/*
	 * Registers the command framework and all necessary commands
	 */
	private void registerCommands()
	{
		commandManager = new PaperCommandManager(this);

		commandManager.registerCommand(new HouseCreationCommands());
	}

	/*
	 * Registers any necessary managers that need to run with onEnable()
	 */
	private void registerManagers()
	{

	}
	
	/*
	 * Registers all listeners pertaining to the Profile class
	 */
	private void registerProfileListeners(PluginManager pluginManager)
	{	
		pluginManager.registerEvents(new ProfileListeners(), this);	
	}
	
	/*
	 * Registers all listeners pertaining to the house package
	 */
	private void registerHouseListeners(PluginManager pluginManager) 
	{
		pluginManager.registerEvents(new FrontDoorListener(), this);
		
		pluginManager.registerEvents(new HouseCreationListeners(), this);
		pluginManager.registerEvents(new HouseEntranceListener(), this);
		pluginManager.registerEvents(new HouseExitListener(), this);
		pluginManager.registerEvents(new FurnitureListeners(), this);
		pluginManager.registerEvents(new OfferHouseListeners(), this);
		pluginManager.registerEvents(new HouseListeners(), this);
		pluginManager.registerEvents(new PlayerJoinListeners(), this);
		pluginManager.registerEvents(new PlayerQuitListeners(), this);
	}

	/*
	 * Loads all house objects from the houses folder using the loadHouse method from House.class
	 */
	private void loadHouses() 
	{	
		File houseFiles = new File(getDataFolder() + "/houses");
		File[] directoryListing = houseFiles.listFiles();
		
		if(directoryListing != null)
		{
			for(File houses : directoryListing)
			{
				House.loadHouse(houses.getName().replace(".yml", ""));
			}
		}
	}
	
	/*
	 * Loading player houses has to come after loading houses. We will load them onEnable for thosue already online. On join, and onHouse purchase.
	 */
	private void loadPlayerHouses()
	{
		for(Profile profiles : com.pattt.instancehousing.profile.Profile.getProfiles())
		{
			Profile.loadPlayerHouses(profiles);
		}
	}
	
	/*
	 * Save all player houses furniture that may have been changed during play.
	 */
	private void updatePlayerFurniture()
	{
		for(Player players : Bukkit.getOnlinePlayers())
		{
			Furniture.updateFurniture(players);	
		}
	}
	
	/*
	 * Loads player and administrator profiles into existance when the server loads up
	 */
	private void loadProfiles()
	{
		for(Player players : Bukkit.getOnlinePlayers())
		{
			if(players.hasPermission("admin"))
			{
				Admin.loadAdminProfile(players);
				players.sendMessage(MessageTool.color("&9Administrator profile for " + players.getName() + " was loaded successfully."));
			}
			else
			{
				Profile.loadProfile(players);
				players.sendMessage(MessageTool.color("&aProfile was successfully loaded."));
			}
		}
	}
}
