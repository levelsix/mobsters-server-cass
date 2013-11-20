package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.TaskStageMonster;

@Component
public class TaskStageMonsterEntityManager extends BaseEntityManager<TaskStageMonster, UUID>{

	
	
	
	
	public TaskStageMonsterEntityManager() {
		super(TaskStageMonster.class, UUID.class);
	}



	


}
