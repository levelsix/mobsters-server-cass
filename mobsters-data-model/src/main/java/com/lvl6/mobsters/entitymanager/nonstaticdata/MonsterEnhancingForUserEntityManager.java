package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;

@Component
public class MonsterEnhancingForUserEntityManager extends BaseEntityManager<MonsterEnhancingForUser, UUID>{

	
	
	
	
	public MonsterEnhancingForUserEntityManager() {
		super(MonsterEnhancingForUser.class, UUID.class);
	}



}
