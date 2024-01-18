package com.pattt.instancehousing.tools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackTool
{
	@SuppressWarnings("deprecation")
	public static ItemStack createItem(Material material, String name, String lore, short durability, int amount)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageTool.color(name));
		ArrayList<String> Lore = new ArrayList<String>();
		if(lore != null)
		{
			Lore.add(MessageTool.color(lore));
			meta.setLore(Lore);
		}
		item.setItemMeta(meta);	
		item.setDurability(durability);
		item.setAmount(amount);

		return item;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack createItemLong(Material material, String name, List<String> list, short durability, int amount)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageTool.color(name));
		ArrayList<String> Lore = new ArrayList<String>();
		if(list != null)
		{
			for(String string : list)
			{
				Lore.add(MessageTool.color(string));	
			}
			meta.setLore(Lore);
		}
		item.setItemMeta(meta);	
		item.setDurability(durability);
		item.setAmount(amount);

		return item;
	}
	public static void createDisplay(Material material, Inventory inv, int Slot, String name)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);

		inv.setItem(Slot, item);
	}
}