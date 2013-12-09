package com.lvl6.mobsters.services.taskforuserongoing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskForUserOngoingEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserOngoing;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class TaskForUserOngoingServiceImpl implements TaskForUserOngoingService {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_TASK_FOR_USER_ONGOING;
	
	
	@Autowired
	protected TaskForUserOngoingEntityManager taskForUserOngoingEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	
	//RETRIEVING STUFF****************************************************************
	@Override
	public TaskForUserOngoing getAllUserTaskForUser(UUID userId) {
		log.debug("retrieve TaskForUserOngoing data for user with id " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.STRUCTURE_FOR_USER__USER_ID, userId);

		//query db, "values" is not used
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, values, preparedStatement);
		List<TaskForUserOngoing> utList = getTaskForUserOngoingEntityManager().get().find(cqlquery);
		
		//error checking. There should only be one row in user_task table for any user
	    if (utList.isEmpty()) {
	      return null;
	    } else {
	      if (utList.size() > 1) {
	        log.error("unexpected error: user has more than one user_task. userTasks=" +
	            utList);
	      }
	      return utList.get(0);
	    }
	}
	
	@Override
	public TaskForUserOngoing getSpecificUserTask(UUID userTaskId) {
		log.debug("retrieve TaskForUserOngoing data for id " + userTaskId);

		TaskForUserOngoing tfuo = getTaskForUserOngoingEntityManager().get().get(userTaskId);
		return tfuo;
	}

	
	
	
	//INSERTING STUFF****************************************************************
	

	//SAVING STUFF****************************************************************

	
	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************



	//for the setter dependency injection or something
	@Override
	public TaskForUserOngoingEntityManager getTaskForUserOngoingEntityManager() {
		return taskForUserOngoingEntityManager;
	}

	@Override
	public void setTaskForUserOngoingEntityManager(TaskForUserOngoingEntityManager taskForUserOngoingEntityManager) {
		this.taskForUserOngoingEntityManager = taskForUserOngoingEntityManager;
	}
	@Override
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	@Override
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
	

	
	//old aoc2 stuff ****************************************************************
	
	
	/*
	@Override
	public Map<String, TaskForUserOngoing> getUdidsToDevicesForUser(UUID userId) {
		Map<String, TaskForUserOngoing> udidsToDevices =
				new HashMap<String, TaskForUserOngoing>();
		
		String cqlQuery = "select * from user_device where user_id = " + userId;
		List<TaskForUserOngoing> devices = getTaskForUserOngoingEntityManager().get().find(cqlQuery);
		
		for (TaskForUserOngoing ud : devices) {
			String udid = ud.getUdid();
			
			udidsToDevices.put(udid, ud);
		}
		
		return udidsToDevices;
	}
	
	@Override
	public void saveTaskForUserOngoings(Collection<TaskForUserOngoing> devices) {
		getTaskForUserOngoingEntityManager().get().put(devices);
	}
	
	 */
	
}