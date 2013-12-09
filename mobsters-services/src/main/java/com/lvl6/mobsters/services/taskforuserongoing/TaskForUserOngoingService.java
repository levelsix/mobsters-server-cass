package com.lvl6.mobsters.services.taskforuserongoing;

import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskForUserOngoingEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserOngoing;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface TaskForUserOngoingService {
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	
	
	//RETRIEVING STUFF****************************************************************
	public abstract TaskForUserOngoing getAllUserTaskForUser(UUID userId);
	
	public abstract TaskForUserOngoing getSpecificUserTask(UUID userTaskId);
	
	
	//INSERTING STUFF****************************************************************
	

	//SAVING STUFF****************************************************************

	
	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************



	//for the setter dependency injection or something
	public abstract TaskForUserOngoingEntityManager getTaskForUserOngoingEntityManager();
	
	public abstract void setTaskForUserOngoingEntityManager(
			TaskForUserOngoingEntityManager taskForUserOngoingEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
	
	
	//old aoc2 stuff ****************************************************************
	/*
	public abstract Map<String, TaskForUserOngoing> getUdidsToDevicesForUser(UUID userId);
	
	public abstract void saveTaskForUserOngoings(Collection<TaskForUserOngoing> devices);
	
	
	
	public abstract TaskForUserOngoingEntityManager getTaskForUserOngoingEntityManager();

	public abstract void setTaskForUserOngoingEntityManager(TaskForUserOngoingEntityManager taskForUserOngoingEntityManager);
	*/
}