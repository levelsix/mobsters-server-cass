package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.UserChest;

@Component
public class UserChestEntityManager extends BaseEntityManager<UserChest, UUID>{

	
	
	
	
	public UserChestEntityManager() {
		super(UserChest.class, UUID.class);
	}



}
