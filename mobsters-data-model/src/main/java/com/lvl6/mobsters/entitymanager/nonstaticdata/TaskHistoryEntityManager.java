package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskHistory;

@Component
public class TaskHistoryEntityManager extends BaseEntityManager<TaskHistory, UUID>{

	
	
	
	
	public TaskHistoryEntityManager() {
		super(TaskHistory.class, UUID.class);
	}




}
