package com.pattt.instancehousing.tools;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationTool 
{
	public static Location getCenter(World world, Location loc) 
	{	
		return new Location(world, loc.getX() + 0.5, loc.getY() + 1, loc.getZ() + 0.5);		
	}

}
