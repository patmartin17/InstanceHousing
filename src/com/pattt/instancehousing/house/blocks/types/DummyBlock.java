package com.pattt.instancehousing.house.blocks.types;

import com.pattt.instancehousing.InstanceHousing;
import com.pattt.instancehousing.house.blocks.BlockHandler;

import lombok.Getter;

public class DummyBlock 
{
	@Getter private InstanceHousing plugin = InstanceHousing.getInstance();
	@Getter private BlockHandler blockHandler = plugin.getBlockHandler();
}
