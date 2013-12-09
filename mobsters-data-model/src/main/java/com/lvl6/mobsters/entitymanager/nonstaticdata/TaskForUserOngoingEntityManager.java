package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserOngoing;

@Component
public class TaskForUserOngoingEntityManager extends BaseEntityManager<TaskForUserOngoing, UUID>{

	
	
	
	
	public TaskForUserOngoingEntityManager() {
		super(TaskForUserOngoing.class, UUID.class);
	}



	


}
