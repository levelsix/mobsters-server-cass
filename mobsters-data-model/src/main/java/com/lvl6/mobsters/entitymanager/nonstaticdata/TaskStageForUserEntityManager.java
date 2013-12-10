package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;

@Component
public class TaskStageForUserEntityManager extends BaseEntityManager<TaskStageForUser, UUID>{

	
	
	
	
	public TaskStageForUserEntityManager() {
		super(TaskStageForUser.class, UUID.class);
	}





}
