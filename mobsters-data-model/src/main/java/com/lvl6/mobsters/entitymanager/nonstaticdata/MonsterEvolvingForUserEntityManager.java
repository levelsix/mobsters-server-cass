package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEvolvingForUser;

@Component
public class MonsterEvolvingForUserEntityManager extends BaseEntityManager<MonsterEvolvingForUser, UUID>{

	
	
	
	
	public MonsterEvolvingForUserEntityManager() {
		super(MonsterEvolvingForUser.class, UUID.class);
	}



}
