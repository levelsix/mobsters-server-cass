package com.lvl6.mobsters.services.taskforusercompleted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskForUserCompletedEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskForUserCompleted;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.services.taskhistory.TaskHistoryService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class TaskForUserCompletedServiceImpl implements TaskForUserCompletedService {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_TASK_FOR_USER_COMPLETED;
	
	
	@Autowired
	protected TaskForUserCompletedEntityManager taskForUserCompletedEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected TaskHistoryService taskHistoryService;
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	
	//RETRIEVING STUFF****************************************************************
	
	//gets all the task ids that the user completed
	@Override
	public Set<Integer> getAllCompletedTaskIdsForUser(UUID userId) {
		log.debug("retrieve TaskForUserCompleted ids for user with id " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.TASK_FOR_USER_COMPLETED__USER_ID, userId);
		String equalityCondDelim = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, values, preparedStatement);
		List<TaskForUserCompleted> tfucList = getTaskForUserCompletedEntityManager().get().find(cqlquery);
		
		Set<Integer> completedTaskIds = new HashSet<Integer>();
		
		for (TaskForUserCompleted tfuc : tfucList) {
			int taskId = tfuc.getTaskId();
			completedTaskIds.add(taskId);
		}
		
		return completedTaskIds;
	}
	

	
	
	
	//INSERTING STUFF****************************************************************
	

	//SAVING STUFF****************************************************************
	@Override
	public void saveTaskForUserCompleted(TaskForUserCompleted tfuo) {
		getTaskForUserCompletedEntityManager().get().put(tfuo);
	}
	
	@Override
	public void saveTaskForUserCompletedList(List<TaskForUserCompleted> tfuoList) {
		getTaskForUserCompletedEntityManager().get().put(tfuoList);
	}

	
	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************


	//for the setter dependency injection or something
	@Override
	public TaskForUserCompletedEntityManager getTaskForUserCompletedEntityManager() {
		return taskForUserCompletedEntityManager;
	}

	@Override
	public void setTaskForUserCompletedEntityManager(TaskForUserCompletedEntityManager taskForUserCompletedEntityManager) {
		this.taskForUserCompletedEntityManager = taskForUserCompletedEntityManager;
	}
	@Override
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	@Override
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
