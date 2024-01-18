package com.pattt.instancehousing.tools;

import org.bukkit.entity.Player;

public class DirectionTool 
{
	public static String getDirection(Player player) 
	{
        double rotation = player.getLocation().getYaw();
        if (rotation < 0) 
        {
            rotation += 360.0;
        }
        if (135 <= rotation && rotation < 225) 
        {
            return "NORTH";
        } 
        else if (225 <= rotation && rotation < 315) 
        {
            return "EAST";
        } 
        else if ((315 <= rotation && rotation < 360) || (0 <= rotation && rotation < 45)) 
        {
            return "SOUTH";
        } 
        else if (45 <= rotation && rotation < 135) 
        {
            return "WEST";
        } 
        return "";
	}
	
	public static void teleportBackward(Player player)
	{
		player.setVelocity(player.getLocation().getDirection().multiply(-1));
	}
}
