package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.UserConsumableQueue;

@Component
public class UserConsumableQueueEntityManager extends BaseEntityManager<UserConsumableQueue, UUID>{

	
	
	
	
	public UserConsumableQueueEntityManager() {
		super(UserConsumableQueue.class, UUID.class);
	}




}
