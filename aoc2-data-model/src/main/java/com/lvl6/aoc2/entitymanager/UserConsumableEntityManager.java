package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserConsumable;

@Component
public class UserConsumableEntityManager extends BaseEntityManager<UserConsumable, UUID>{

	
	
	
	
	public UserConsumableEntityManager() {
		super(UserConsumable.class, UUID.class);
	}




}
