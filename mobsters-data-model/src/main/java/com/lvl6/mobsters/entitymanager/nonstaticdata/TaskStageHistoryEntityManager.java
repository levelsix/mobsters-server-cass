package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageHistory;

@Component
public class TaskStageHistoryEntityManager extends BaseEntityManager<TaskStageHistory, UUID>{

	
	
	
	
	public TaskStageHistoryEntityManager() {
		super(TaskStageHistory.class, UUID.class);
	}




}
