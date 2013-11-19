package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;

@Component
public class MonsterForUserEntityManager extends BaseEntityManager<MonsterForUser, UUID>{

	
	
	
	
	public MonsterForUserEntityManager() {
		super(MonsterForUser.class, UUID.class);
	}

	


}
