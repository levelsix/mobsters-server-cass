package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.TaskStage;

@Component
public class TaskStageEntityManager extends BaseEntityManager<TaskStage, Integer>{

	
	
	
	
	public TaskStageEntityManager() {
		super(TaskStage.class, Integer.class);
	}




}
