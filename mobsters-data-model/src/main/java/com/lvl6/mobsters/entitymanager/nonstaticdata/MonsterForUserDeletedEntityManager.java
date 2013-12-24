package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUserDeleted;

@Component
public class MonsterForUserDeletedEntityManager extends BaseEntityManager<MonsterForUserDeleted, UUID>{

	
	
	
	
	public MonsterForUserDeletedEntityManager() {
		super(MonsterForUserDeleted.class, UUID.class);
	}

	


}
