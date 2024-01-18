package com.pattt.instancehousing.house.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import com.pattt.instancehousing.house.Furniture;
import com.pattt.instancehousing.house.blocks.types.ChestManager;
import com.pattt.instancehousing.house.managers.BlockManager;

import lombok.Getter;

public class BlockHandler 
{
	// put clickedBlock
	// We will be able to recreated this variable using private BlockHandler handler = plugin.getBlockHandler();
	// then they can do handler.getClickedBlocks();
	// clicked Block Face
	// placing Block hashmaps. Make sure to handle them often.
	//Make sure they are all getters and setters so we are not refrencing variables. Makes it so much easier to read.
	
	// this class will be instantiated onEnable and be refrencable using plugin.getBlockHandler() because it will be a variable in the main class
	// this way we can do ChestManager manager = plugin.getBlockHandler().getChestManager();
	// ChestManager manager = plugin.getBlockHandler().getChestManager();
	
	@Getter public final Map<Player, BlockFace> clickedBlockFaceMap = new HashMap<Player, BlockFace>();
	
	@Getter public final Map<Player, Location> clickedBlockMap = new HashMap<Player, Location>();

	@Getter public final List<Player> placingBlockMap = new ArrayList<Player>();
	
	@Getter public ChestManager chestManager;
	
	public void loadBlockManagers()
	{
		chestManager = new ChestManager();
	}
	
	public boolean clickedInteractableBlock(Player player)
	{
		if(getClickedBlockMap().containsKey(player))
		{
			return true;
		}
		return false;
	}
	
	public Location getClickedBlockLocation(Player player)
	{
		if(getClickedBlockMap().containsKey(player))
		{
			return getClickedBlockMap().get(player);
		}
		return null;
	}
	
	public BlockFace getClickedBlockFace(Player player)
	{
		if(getClickedBlockFaceMap().containsKey(player))
		{
			return getClickedBlockFaceMap().get(player);
		}
		return null;
	}
	
	public void placeFurnitureBlock(Player player, Furniture furniture, Location location, BlockData blockData)
	{
		furniture.getFurnitureList().put(location, blockData);
		BlockManager.sendSingleBlockChange(player, location, blockData);
	}
}
