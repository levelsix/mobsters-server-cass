package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingHistory;

@Component
public class MonsterHealingHistoryEntityManager extends BaseEntityManager<MonsterHealingHistory, UUID>{

	
	
	
	
	public MonsterHealingHistoryEntityManager() {
		super(MonsterHealingHistory.class, UUID.class);
	}




}
