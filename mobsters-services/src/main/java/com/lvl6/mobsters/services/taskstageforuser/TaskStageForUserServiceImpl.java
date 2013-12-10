package com.lvl6.mobsters.services.taskstageforuser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.TaskStageForUserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.TaskStageForUser;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class TaskStageForUserServiceImpl implements TaskStageForUserService {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_TASK_FOR_USER_ONGOING;
	
	
	@Autowired
	protected TaskStageForUserEntityManager taskStageForUserEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	
	//RETRIEVING STUFF****************************************************************
	@Override
	public List<TaskStageForUser> getAllUserTaskForUserTask(UUID userTaskId) {
		log.debug("retrieve TaskStageForUser data for user task id " + userTaskId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.TASK_STAGE_FOR_USER__TASK_FOR_USER_ID, userTaskId);

		//query db, "values" is not used
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, values, preparedStatement);
		List<TaskStageForUser> tsfuList = getTaskStageForUserEntityManager().get().find(cqlquery);
		
		return tsfuList;
	}
	
	@Override
	public TaskStageForUser getSpecificTaskStageForUser(UUID taskStageForUserId) {
		log.debug("retrieve TaskStageForUser data for id " + taskStageForUserId);

		TaskStageForUser tfuo = getTaskStageForUserEntityManager().get().get(taskStageForUserId);
		return tfuo;
	}

	
	
	
	//INSERTING STUFF****************************************************************
	

	//SAVING STUFF****************************************************************

	
	//UPDATING STUFF****************************************************************

	//DELETING STUFF****************************************************************



	//for the setter dependency injection or something
	@Override
	public TaskStageForUserEntityManager getTaskStageForUserEntityManager() {
		return taskStageForUserEntityManager;
	}

	@Override
	public void setTaskStageForUserEntityManager(TaskStageForUserEntityManager taskStageForUserEntityManager) {
		this.taskStageForUserEntityManager = taskStageForUserEntityManager;
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