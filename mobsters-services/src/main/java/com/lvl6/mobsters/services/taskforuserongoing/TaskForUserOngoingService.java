package com.lvl6.mobsters.services.taskforuserongoing;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskForUserOngoingEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserOngoing;
import com.lvl6.mobsters.services.taskhistory.TaskHistoryService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface TaskForUserOngoingService {
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	
	
	//RETRIEVING STUFF****************************************************************
	public abstract TaskForUserOngoing getAllUserTaskForUser(UUID userId);
	
	public abstract TaskForUserOngoing getSpecificUserTask(UUID userTaskId);
	
	
	//INSERTING STUFF****************************************************************
	

	//SAVING STUFF****************************************************************
	public abstract void saveTaskForUserOngoing(TaskForUserOngoing tfuo);
	
	public abstract void saveTaskForUserOngoingList(List<TaskForUserOngoing> tfuoList);
	
	//UPDATING STUFF****************************************************************
	public abstract void updateIncrementUserTaskNumRevives(UUID userTaskId, int numRevivesDelta);

	//DELETING STUFF****************************************************************
	
	public abstract void deleteExistingUserTask(UUID userTaskId, Date endDate,
			boolean userWon, boolean cancelled,  TaskForUserOngoing existing);

	public abstract void deleteUserTask(UUID userTaskId);
	
	public abstract void deleteUserTasks(List<UUID> userTaskIdList);


	//for the setter dependency injection or something
	public abstract TaskForUserOngoingEntityManager getTaskForUserOngoingEntityManager();
	
	public abstract void setTaskForUserOngoingEntityManager(
			TaskForUserOngoingEntityManager taskForUserOngoingEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
	public abstract TaskHistoryService getTaskHistoryService();
	
	public abstract void setTaskHistoryService(TaskHistoryService taskHistoryService);
	
	//old aoc2 stuff ****************************************************************
	/*
	public abstract Map<String, TaskForUserOngoing> getUdidsToDevicesForUser(UUID userId);
	
	public abstract void saveTaskForUserOngoings(Collection<TaskForUserOngoing> devices);
	
	
	
	public abstract TaskForUserOngoingEntityManager getTaskForUserOngoingEntityManager();

	public abstract void setTaskForUserOngoingEntityManager(TaskForUserOngoingEntityManager taskForUserOngoingEntityManager);
	*/
}