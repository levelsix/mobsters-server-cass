package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserEquipRepair;

@Component
public class UserEquipRepairEntityManager extends BaseEntityManager<UserEquipRepair, UUID>{

	
	
	
	
	public UserEquipRepairEntityManager() {
		super(UserEquipRepair.class, UUID.class);
	}




}
