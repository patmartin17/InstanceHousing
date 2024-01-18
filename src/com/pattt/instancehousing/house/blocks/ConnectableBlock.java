package com.pattt.instancehousing.house.blocks;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.PlayerHouse;
import com.pattt.instancehousing.house.blocks.types.ChestManager;
import com.pattt.instancehousing.profile.Profile;


public enum ConnectableBlock 
{
	/*
	 * Everything here may have to become its own object because some things will have too much data to fit in this enum
	 */

	TRAPPED_CHEST {
		@Override
		public void handleInteraction(Player player, Location location, BlockData blockData) 
		{

		}

	},

	CHEST {
		@Override
		public void handleInteraction(Player player, Location location, BlockData blockData) 
		{
			ChestManager manager = plugin.getBlockHandler().getChestManager();
			
			Profile profile = Profile.getByPlayer(player);
			
			PlayerHouse playerHouse = PlayerHouse.getPlayerHouse(profile, profile.getHouse());
			
			Furniture furniture = playerHouse.getFurniture();
			
			manager.handleConnection(player, furniture, location, blockData);
		}
	};

	public abstract void handleInteraction(Player player, Location location, BlockData blockData);
	
	private static InstanceHousing plugin = InstanceHousing.getInstance();
}
