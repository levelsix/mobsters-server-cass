package com.lvl6.mobsters.services.taskhistory;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserOngoing;
import com.lvl6.mobsters.po.nonstaticdata.TaskHistory;


public interface TaskHistoryService {

	//CONTROLLER LOGIC STUFF****************************************************************



	//RETRIEVING STUFF****************************************************************


	//INSERTING STUFF****************************************************************
	public abstract void insertUserTaskIntoHistory(UUID userTaskId, Date endDate,
			boolean userWon, boolean cancelled, TaskForUserOngoing aTaskForUser);

	//SAVING STUFF****************************************************************
	public abstract void saveTaskHistory(TaskHistory th);
	
	public abstract void saveTaskHistories(List<TaskHistory> thList);

	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************



	//for the setter dependency injection or something
	public abstract TaskHistoryEntityManager getTaskHistoryEntityManager();
	
	public abstract void setTaskHistoryEntityManager(
			TaskHistoryEntityManager taskHistoryEntityManager);
}
