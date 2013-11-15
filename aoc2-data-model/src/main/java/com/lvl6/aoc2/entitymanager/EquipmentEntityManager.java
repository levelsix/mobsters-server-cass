package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.Equipment;

@Component
public class EquipmentEntityManager extends BaseEntityManager<Equipment, UUID>{

	
	
	
	
	public EquipmentEntityManager() {
		super(Equipment.class, UUID.class);
	}


	


}
