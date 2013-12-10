package com.lvl6.mobsters.services.taskstageforuser;

import java.util.List;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskStageForUserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface TaskStageForUserService {
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	
	
	//RETRIEVING STUFF****************************************************************
	public abstract List<TaskStageForUser> getAllUserTaskForUserTask(UUID userTaskId);
	
	public abstract TaskStageForUser getSpecificTaskStageForUser(UUID taskStageForUserId);
	
	
	//INSERTING STUFF****************************************************************
	

	//SAVING STUFF****************************************************************

	
	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************



	//for the setter dependency injection or something
	public abstract TaskStageForUserEntityManager getTaskStageForUserEntityManager();
	
	public abstract void setTaskStageForUserEntityManager(
			TaskStageForUserEntityManager taskStageForUserEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}
