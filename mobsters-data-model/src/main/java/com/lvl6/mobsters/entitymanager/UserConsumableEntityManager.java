package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.UserConsumable;

@Component
public class UserConsumableEntityManager extends BaseEntityManager<UserConsumable, UUID>{

	
	
	
	
	public UserConsumableEntityManager() {
		super(UserConsumable.class, UUID.class);
	}




}
