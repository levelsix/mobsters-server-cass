package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.MonsterPersistentDrop;

@Component
public class MonsterPersistentDropEntityManager extends BaseEntityManager<MonsterPersistentDrop, UUID>{

	
	
	
	
	public MonsterPersistentDropEntityManager() {
		super(MonsterPersistentDrop.class, UUID.class);
	}




}
