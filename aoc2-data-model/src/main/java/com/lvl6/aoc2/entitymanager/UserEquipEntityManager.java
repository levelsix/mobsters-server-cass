package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserEquip;

@Component
public class UserEquipEntityManager extends BaseEntityManager<UserEquip, UUID>{

	
	
	
	
	public UserEquipEntityManager() {
		super(UserEquip.class, UUID.class);
	}

	


}
