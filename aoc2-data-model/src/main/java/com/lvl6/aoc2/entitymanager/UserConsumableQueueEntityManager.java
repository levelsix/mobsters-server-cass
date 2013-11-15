package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserConsumableQueue;

@Component
public class UserConsumableQueueEntityManager extends BaseEntityManager<UserConsumableQueue, UUID>{

	
	
	
	
	public UserConsumableQueueEntityManager() {
		super(UserConsumableQueue.class, UUID.class);
	}




}
