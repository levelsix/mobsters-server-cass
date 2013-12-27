package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingHistory;

@Component
public class MonsterEnhancingHistoryEntityManager extends BaseEntityManager<MonsterEnhancingHistory, UUID>{

	
	
	
	
	public MonsterEnhancingHistoryEntityManager() {
		super(MonsterEnhancingHistory.class, UUID.class);
	}



}
