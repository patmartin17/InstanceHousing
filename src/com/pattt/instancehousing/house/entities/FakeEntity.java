package com.pattt.instancehousing.house.entities;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.pattt.instancehousing.house.PlayerHouse;

import lombok.Getter;
import lombok.Setter;

public class FakeEntity 
{
	public enum FakeEntityType
	{
		ITEM_DROP,
		MONSTER,
		PET,
		BLOCK,
		FALLING_BLOCK;
	}
	
	@Getter @Setter public Map<Location, Entity> entityList;
	@Getter @Setter public Entity entity;
	@Getter @Setter public Location location;
	@Getter @Setter public PlayerHouse playerHouse;
	@Getter @Setter public FakeEntityType fakeEntityType;
	
	public FakeEntity(Entity entity, Location location, PlayerHouse playerHouse, FakeEntityType fakeEntityType)
	{
		setFakeEntityType(fakeEntityType);
		setPlayerHouse(playerHouse);
	}
	
	public void deleteFakeEntity()
	{
			
	}
	
	public void saveFakeEntity()
	{
		
	}
	
	public void loadFakeEntity()
	{
		//switch () 
		//{
		//case value: 
		//{
		//	
		//	yield type;
		//}
		//default:
		//	throw new IllegalArgumentException("Unexpected value: " + key);
		//}
	}
	
	
}
