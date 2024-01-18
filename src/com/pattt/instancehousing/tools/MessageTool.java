package com.pattt.instancehousing.tools;

import org.bukkit.ChatColor;

public class MessageTool 
{
	public static String prefix = color("&9&lHousing &7&l ");
	
	public static String color(String string)
	{
		return ChatColor.translateAlternateColorCodes('&', string);				
	}
}
