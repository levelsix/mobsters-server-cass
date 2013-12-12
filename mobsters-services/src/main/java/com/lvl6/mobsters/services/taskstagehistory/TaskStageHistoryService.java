package com.lvl6.mobsters.services.taskstagehistory;

import java.util.List;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskStageHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageHistory;

public interface TaskStageHistoryService {

	//CONTROLLER LOGIC STUFF****************************************************************



	//RETRIEVING STUFF****************************************************************


	//INSERTING STUFF****************************************************************
//	public abstract void insertUserTaskStagesIntoHistory(UUID userTaskId,
//			List<TaskStageForUser> taskStagesForUser);

	//SAVING STUFF****************************************************************
	public abstract void saveTaskStageHistory(TaskStageHistory tsh);

	public abstract void saveTaskStageHistories(List<TaskStageHistory> tshList);

	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************



	//for the setter dependency injection or something
	public abstract TaskStageHistoryEntityManager getTaskStageHistoryEntityManager();

	public abstract void setTaskStageHistoryEntityManager(
			TaskStageHistoryEntityManager taskHistoryEntityManager);

}