package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserChest;

@Component
public class UserChestEntityManager extends BaseEntityManager<UserChest, UUID>{

	
	
	
	
	public UserChestEntityManager() {
		super(UserChest.class, UUID.class);
	}



}
