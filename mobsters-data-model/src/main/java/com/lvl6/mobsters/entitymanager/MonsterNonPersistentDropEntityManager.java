package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.MonsterNonPersistentDrop;

@Component
public class MonsterNonPersistentDropEntityManager extends BaseEntityManager<MonsterNonPersistentDrop, UUID>{

	
	
	
	
	public MonsterNonPersistentDropEntityManager() {
		super(MonsterNonPersistentDrop.class, UUID.class);
	}





}
