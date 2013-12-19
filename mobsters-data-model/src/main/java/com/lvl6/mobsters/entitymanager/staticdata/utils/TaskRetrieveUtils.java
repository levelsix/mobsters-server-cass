package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.TaskEntityManager;
import com.lvl6.mobsters.po.staticdata.Task;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class TaskRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private Map<Integer, List<Task>> cityIdsToTasks;
	private Map<Integer, Task> idsToTasks;

	private final String TABLE_NAME = MobstersDbTables.TABLE_TASK;

	
	
	@Autowired
	protected TaskEntityManager taskEntityManager;
	
	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	
	public Map<Integer, Task> getTaskIdsToTasks() {
		if (null == idsToTasks) {
			setStaticIdsToTasks();
		}
		return idsToTasks;
	}
	

	public Task getTaskForId(Integer id) {
		log.debug("retrieve task data for id " + id);
		if (null == idsToTasks) {
			setStaticIdsToTasks();      
		}
		return idsToTasks.get(id);
	}

	public  Map<Integer, Task> getTasksForIds(List<Integer> ids) {
		log.debug("retrieve tasks data for ids " + ids);
		if (null == idsToTasks) {
			setStaticIdsToTasks();      
		}
		Map<Integer, Task> toreturn = new HashMap<Integer, Task>();
		for (Integer id : ids) {
			toreturn.put(id,  idsToTasks.get(id));
		}
		return toreturn;
	}
	
	public List<Task> getAllTasksForCityId(int cityId) {
		log.debug("retrieving all tasks for cityId " + cityId);
		if (null == cityIdsToTasks) {
			setStaticCityIdsToTasks();
		}
		return cityIdsToTasks.get(cityId);
	}

	public Set<Integer> getAllTaskIdsForCityId(int cityId) {
		log.debug("retrieving all taskIds for cityId=" + cityId);
		if (null == cityIdsToTasks) {
			setStaticCityIdsToTasks();
		}
		List<Task> tasksForCity = null;
		tasksForCity = cityIdsToTasks.get(cityId);

		if (null == tasksForCity) {
			return new HashSet<Integer>();
		}

		Set<Integer> retVal = new HashSet<Integer>();
		for (Task t : tasksForCity) {
			int taskId = t.getId();
			retVal.add(taskId);
		}
		return retVal;
	}

	public int getCityIdForTask(int taskId) {
		if (null == cityIdsToTasks) {
			setStaticCityIdsToTasks();
		}


		if (!idsToTasks.containsKey(taskId)) {
			return 0;
		}

		Task aTask = idsToTasks.get(taskId);
		int cityId = aTask.getCityId();
		return cityId;
	}
	
	private void setStaticCityIdsToTasks() {
		if (null == idsToTasks) {
			setStaticIdsToTasks();
			
		} else if (null == cityIdsToTasks) {
			cityIdsToTasks = new HashMap<Integer, List<Task>>();
			for(Task aTask : idsToTasks.values()) {
				//store in city task map
				int cityId = aTask.getCityId();
				//check base case where no tasks for city is recorded
				if (!cityIdsToTasks.containsKey(cityId)) {
					List<Task> cityTasks = new ArrayList<Task>();
					cityIdsToTasks.put(cityId, cityTasks);
				}
				//steady state case where tasks for city is recorded
				List<Task> cityTasks = cityIdsToTasks.get(cityId);
				cityTasks.add(aTask);
			}
		} // else, things should be set
		
	}

	
	private void setStaticIdsToTasks() {
		log.debug("setting  map of taskIds to tasks");

		//construct the search parameters, null to get whole table
		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<Task> taskList = getTaskEntityManager().get().find(cqlquery);
		
		//fill up the maps
		idsToTasks = new HashMap<Integer, Task>();
		cityIdsToTasks = new HashMap<Integer, List<Task>>();
		for(Task aTask : taskList) {
			//store in task map
			int intId = aTask.getId();
			idsToTasks.put(intId, aTask);
			
			
			//store in city task map
			int cityId = aTask.getCityId();
			//check base case where no tasks for city is recorded
			if (!cityIdsToTasks.containsKey(cityId)) {
				List<Task> cityTasks = new ArrayList<Task>();
				cityIdsToTasks.put(cityId, cityTasks);
			}
			//steady state case where tasks for city is recorded
			List<Task> cityTasks = cityIdsToTasks.get(cityId);
			cityTasks.add(aTask);
		}
	}

	public void reload() {
		setStaticIdsToTasks();
	}
	
	

	public TaskEntityManager getTaskEntityManager() {
		return taskEntityManager;
	}

	public void setTaskEntityManager(
			TaskEntityManager taskEntityManager) {
		this.taskEntityManager = taskEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
