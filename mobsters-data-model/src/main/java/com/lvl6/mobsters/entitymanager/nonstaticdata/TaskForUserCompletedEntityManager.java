package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserCompleted;

@Component
public class TaskForUserCompletedEntityManager extends BaseEntityManager<TaskForUserCompleted, UUID>{

	
	
	
	
	public TaskForUserCompletedEntityManager() {
		super(TaskForUserCompleted.class, UUID.class);
	}



	


}
