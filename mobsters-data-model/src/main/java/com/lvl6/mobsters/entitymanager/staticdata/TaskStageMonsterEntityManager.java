package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.TaskStageMonster;

@Component
public class TaskStageMonsterEntityManager extends BaseEntityManager<TaskStageMonster, Integer>{

	
	
	
	
	public TaskStageMonsterEntityManager() {
		super(TaskStageMonster.class, Integer.class);
	}



	


}
