package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.MonsterLevelInfo;

@Component
public class MonsterLevelInfoEntityManager extends BaseEntityManager<MonsterLevelInfo, Integer>{

	
	
	
	
	public MonsterLevelInfoEntityManager() {
		super(MonsterLevelInfo.class, Integer.class);
	}


	
	


}
