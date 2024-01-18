package com.pattt.instancehousing.tools;

public class NumberTool 
{
	public static boolean isPositive(int x)
	{
		if(x > 0)
			return true;
		return false;
	}
	
	public static boolean isPositive(double x)
	{
		if(x > 0.0)
			return true;
		return false;
	}
}
