package com.lvl6.mobsters.services.taskforusercompleted;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskForUserCompletedEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserCompleted;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface TaskForUserCompletedService {
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	
	
	//RETRIEVING STUFF****************************************************************
	public abstract Set<Integer> getAllCompletedTaskIdsForUser(UUID userId);
	
	
	//INSERTING STUFF****************************************************************
	

	//SAVING STUFF****************************************************************
	public abstract void saveTaskForUserCompleted(TaskForUserCompleted tfuo);
	
	public abstract void saveTaskForUserCompletedList(List<TaskForUserCompleted> tfuoList);
	
	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************
	

	//for the setter dependency injection or something
	public abstract TaskForUserCompletedEntityManager getTaskForUserCompletedEntityManager();
	
	public abstract void setTaskForUserCompletedEntityManager(
			TaskForUserCompletedEntityManager taskForUserCompletedEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}
