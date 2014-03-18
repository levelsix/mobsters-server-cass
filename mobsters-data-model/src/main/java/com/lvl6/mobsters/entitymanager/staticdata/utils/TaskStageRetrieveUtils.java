package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.TaskStageEntityManager;
import com.lvl6.mobsters.po.staticdata.TaskStage;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class TaskStageRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private Map<Integer, Map<Integer, TaskStage>> taskIdsToTaskStageIdsToTaskStages;
	private Map<Integer, TaskStage> taskStageIdsToTaskStages;

	private final String TABLE_NAME = MobstersDbTables.TABLE_TASK_STAGE;
	
	@Autowired
	protected TaskStageEntityManager taskStageEntityManager;
	
	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	

	public Map<Integer, Map<Integer, TaskStage>> gettaskIdsToTaskStageIdsToTaskStages() {
		log.debug("retrieving all task stage data map");
		if (taskIdsToTaskStageIdsToTaskStages == null) {
			setStatictaskIdsToTaskStageIdsToTaskStages();
		}
		return taskIdsToTaskStageIdsToTaskStages;
	}
	
	public TaskStage getTaskStageForTaskStageId(int taskStageId) {
		if (taskStageIdsToTaskStages == null) {
			setStatictaskIdsToTaskStageIdsToTaskStages();      
		}
		return taskStageIdsToTaskStages.get(taskStageId); 
	}

	public Map<Integer, TaskStage> getTaskStagesForTaskId(int taskId) {
		log.debug("retrieve monster data for monster " + taskId);
		if (taskIdsToTaskStageIdsToTaskStages == null) {
			setStatictaskIdsToTaskStageIdsToTaskStages();      
		}
		return taskIdsToTaskStageIdsToTaskStages.get(taskId);
	}
	
	private void setStatictaskIdsToTaskStageIdsToTaskStages() {
		if (null == taskStageIdsToTaskStages) {
			setStaticTaskStageIdsToTaskStages();
			
		} else if (null == taskIdsToTaskStageIdsToTaskStages) {
			taskIdsToTaskStageIdsToTaskStages = new HashMap<Integer, Map<Integer, TaskStage>>();
			for(TaskStage aTaskStage : taskStageIdsToTaskStages.values()) {
				//store in task to task stage map 
				int taskId = aTaskStage.getTaskId();
				
				//check base case where no task stages for task is recorded
				if (!taskIdsToTaskStageIdsToTaskStages.containsKey(taskId)) {
					Map<Integer, TaskStage> taskStages = new HashMap<Integer, TaskStage>();
					taskIdsToTaskStageIdsToTaskStages.put(taskId, taskStages);
				}
				
				//steady state case where task stages for task is recorded
				Map<Integer, TaskStage> taskStages = taskIdsToTaskStageIdsToTaskStages.get(taskId);
				int taskStageId = aTaskStage.getId();
				taskStages.put(taskStageId, aTaskStage);
			}
		} // else, things should be set
		
	}

	
	private void setStaticTaskStageIdsToTaskStages() {
		log.debug("setting  map of taskIds to tasks");

		//construct the search parameters, null to get whole table
		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		boolean allowFiltering = false; //don't let cassandra query with non row keys
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values,
				preparedStatement, allowFiltering);
		List<TaskStage> taskStageList = getTaskStageEntityManager().get().find(cqlquery);
		
		//fill up the maps
		taskStageIdsToTaskStages = new HashMap<Integer, TaskStage>();
		taskIdsToTaskStageIdsToTaskStages = new HashMap<Integer, Map<Integer, TaskStage>>();
		for(TaskStage aTaskStage : taskStageList) {
			//store in task stage map
			int intId = aTaskStage.getId();
			taskStageIdsToTaskStages.put(intId, aTaskStage);
			
			
			//store in task to task stage map 
			int taskId = aTaskStage.getTaskId();
			
			//check base case where no task stages for task is recorded
			if (!taskIdsToTaskStageIdsToTaskStages.containsKey(taskId)) {
				Map<Integer, TaskStage> taskStages = new HashMap<Integer, TaskStage>();
				taskIdsToTaskStageIdsToTaskStages.put(taskId, taskStages);
			}
			
			//steady state case where task stages for task is recorded
			Map<Integer, TaskStage> taskStages = taskIdsToTaskStageIdsToTaskStages.get(taskId);
			int taskStageId = aTaskStage.getId();
			taskStages.put(taskStageId, aTaskStage);
		}
	}

	public void reload() {
		setStaticTaskStageIdsToTaskStages();
	}
	
	

	public TaskStageEntityManager getTaskStageEntityManager() {
		return taskStageEntityManager;
	}

	public void setTaskStageEntityManager(
			TaskStageEntityManager taskStageEntityManager) {
		this.taskStageEntityManager = taskStageEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
