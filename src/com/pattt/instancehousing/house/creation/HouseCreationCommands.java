package com.pattt.instancehousing.house.creation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pattt.instancehousing.house.House;
import com.pattt.instancehousing.profile.Admin;
import com.pattt.instancehousing.tools.ItemStackTool;
import com.pattt.instancehousing.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import lombok.Getter;
import lombok.Setter;

@CommandAlias("house")
@Description("House creation command.")
public class HouseCreationCommands extends BaseCommand 
{
	@Getter @Setter public static ItemStack houseWand = ItemStackTool.createItem(Material.GOLDEN_HOE, MessageTool.color("&9&lHouse Wand"), null, (short) 0, 1);
	
	@Default
    @Syntax("&cUsage: /house create <name>")
	public void houseCreationCommand(CommandSender sender)
	{
        sender.sendMessage(MessageTool.color("&cUsage: /house create <name>"));
	}
	
    @Subcommand("create")
    @CommandPermission("admin")
    @Syntax("create <name>")
    public void create(CommandSender sender, String houseName) 
    {
    	Player player = Bukkit.getPlayer(sender.getName());
    	Admin adminProfile = Admin.getByUUID(player.getUniqueId());
    	
    	if(House.getHouseByName(houseName) != null)
    	{
			player.sendMessage(MessageTool.color(MessageTool.prefix + "&cA house with that name already exisits."));
			return;
    	}
    	
    	adminProfile.setInHouseCreation(true);
    	Admin.houseCreationMap.put(player, houseName);
    	//MAKE A GUI FOR THE PLAYER TO DECIDE WHAT TO MAKE
    	
    	for(String string : begunCreationMessage(houseName))
    	{
    		player.sendMessage(string);
    	}
    	
    	ItemMeta houseWandMeta = houseWand.getItemMeta();
    	houseWandMeta.setLore(wandLore());
    	houseWand.setItemMeta(houseWandMeta);
    	
    	giveAdminInventory(player);
    }
    
    @Subcommand("update")
    @CommandPermission("admin")
    @Syntax("update <name>")
    public void update(CommandSender sender, String houseName) 
    {	
    	Player player = Bukkit.getPlayer(sender.getName());
    	House house = House.getHouseByName(houseName);
    	if(house != null)
    	{
    		house.updateCurrentBlocks();
			player.sendMessage(MessageTool.color(MessageTool.prefix + "&aYou have updated " + houseName + "."));
    	}
    }
    
    @Subcommand("delete")
    @CommandPermission("admin")
    @Syntax("delete <name>")
    public void delete(CommandSender sender, String houseName) 
    {
    	//REMOVE FROM HOUSE OBJECT, REMOVE ALL PLAYERS WITH THIS CURRENT HOUSE, REMOVE ALL FURNiTURE UNITS WITH THIS HOUSE AND REMOVE THE FILE.
    }
    
    @Subcommand("set")
    @CommandPermission("admin")
    @Syntax("set <name>")
    public void set(CommandSender sender, String houseName) 
    {
    	//ALL THE CHANGING OF CERTAIN INFORMATION OF AN ALREADY EXISITING HOUSE
    	// THIS INCLUDES AMOUNT OF DOORS, DOOR LOCATIONS, HOUSE BOUNDARIES, ETC.
    }
    
    @SuppressWarnings("deprecation")
	private void giveAdminInventory(Player player)
	{	
    	ItemStack currentItem = player.getItemInHand();
    	
    	player.setItemInHand(houseWand);
    	player.getInventory().addItem(currentItem);
    }
    
    private List<String> begunCreationMessage(String houseName)
    {
    	List<String> messageList = new ArrayList<String>();
    	
    	messageList.add(MessageTool.color("&7&m-----------------------[&9&lHousing&7&m]-----------------------"));
    	messageList.add(MessageTool.color("&aYou have begun the creation of a house by the name of: &f" + houseName + "&a."));
    	messageList.add(MessageTool.color("&aSelect two opposite points that encompass the entire house and"));
    	messageList.add(MessageTool.color("&afinish by selecting a front door."));
    	messageList.add(MessageTool.color("&7&m-------------------------------------------------------"));
    	
    	return messageList;
    }
    
    private List<String> wandLore()
    {
    	List<String> wandLore = new ArrayList<String>();
    	
    	wandLore.add(MessageTool.color("&7&m---------------------------------"));
    	wandLore.add(MessageTool.color("&eLeft Click &7to select &ePosition 1"));
    	wandLore.add(MessageTool.color("&eRight Click &7to select &ePosition 2"));
    	wandLore.add(MessageTool.color("&eShift + Left Click &7to select &eFront Door"));
    	wandLore.add(MessageTool.color("&eShift + Right Click &7to create."));
    	wandLore.add(MessageTool.color("&7&m---------------------------------"));
    	
    	return wandLore;
    }
}
