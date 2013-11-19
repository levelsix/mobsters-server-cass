package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;

@Component
public class MonsterHealingForUserEntityManager extends BaseEntityManager<MonsterHealingForUser, UUID>{

	
	
	
	
	public MonsterHealingForUserEntityManager() {
		super(MonsterHealingForUser.class, UUID.class);
	}




}
