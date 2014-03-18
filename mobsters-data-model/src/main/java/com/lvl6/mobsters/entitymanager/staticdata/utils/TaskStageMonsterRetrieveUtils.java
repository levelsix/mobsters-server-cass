package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.TaskStageMonsterEntityManager;
import com.lvl6.mobsters.po.staticdata.TaskStageMonster;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class TaskStageMonsterRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private Map<Integer, List<TaskStageMonster>> taskStageIdsToTaskStageMonsters;
	private Map<Integer, TaskStageMonster> taskStageMonsterIdsToTaskStageMonsters;

	private final String TABLE_NAME = MobstersDbTables.TABLE_TASK_STAGE_MONSTER;

	@Autowired
	protected TaskStageMonsterEntityManager taskStageMonsterEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	
	public Map<Integer, List<TaskStageMonster>> getTaskStageIdsToTaskStageMonsters() {
		log.debug("retrieving all task stage monster data map");
		if (taskStageIdsToTaskStageMonsters == null) {
			setStaticTaskStageIdsToTaskStageMonster();
		}
		return taskStageIdsToTaskStageMonsters;
	}

	public List<TaskStageMonster> getTaskStagesForTaskStageId(int taskStageId) {
		log.debug("retrieve task stage data for stage " + taskStageId);
		if (taskStageIdsToTaskStageMonsters == null) {
			setStaticTaskStageIdsToTaskStageMonster();      
		}
		return taskStageIdsToTaskStageMonsters.get(taskStageId);
	}
	
	public Map<Integer, TaskStageMonster> getTaskStageMonstersForIds(Collection<Integer> ids) {
		if (null == taskStageMonsterIdsToTaskStageMonsters) {
			setStaticTaskStageIdsToTaskStageMonster();
		}
		Map<Integer, TaskStageMonster> returnMap = new HashMap<Integer, TaskStageMonster>();

		for (int id : ids) {
			TaskStageMonster tsm = taskStageMonsterIdsToTaskStageMonsters.get(id);
			returnMap.put(id, tsm);
		}
		return returnMap;
	}

	

	private  void setStaticTaskStageIdsToTaskStageMonster() {
		log.debug("setting static map of taskStage and taskStageMonster Ids to monsterIds");

		//construct the search parameters
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
		List <TaskStageMonster> list = getTaskStageMonsterEntityManager().get().find(cqlquery);
		
		//fill up the map
		taskStageIdsToTaskStageMonsters = new HashMap<Integer, List<TaskStageMonster>>();
		taskStageMonsterIdsToTaskStageMonsters = new HashMap<Integer, TaskStageMonster>();

		//used for logic regarding computing cash reward and if a piece dropped
		Random rand = new Random();
		for(TaskStageMonster tsm : list) {
			tsm.setRand(rand);
			
			//ensuring that enum string is stripped of white space and capitalized
			String monsterType = tsm.getMonsterType();
			String newMonsterType = monsterType.trim().toUpperCase(Locale.ENGLISH);
			if (!monsterType.equals(newMonsterType)) {
				log.error("Monster type incorrectly set for task stage monster. monster=" + tsm);
			}
			tsm.setMonsterType(newMonsterType);
			
			int stageId = tsm.getStageId();
			if (!taskStageIdsToTaskStageMonsters.containsKey(stageId)) {
				//just say first monster for stageId, initialize list
				taskStageIdsToTaskStageMonsters.put(stageId, new ArrayList<TaskStageMonster>());
			}
			//fill up one static data collection
			List<TaskStageMonster> monsters = taskStageIdsToTaskStageMonsters.get(stageId);
			monsters.add(tsm);

			//fill up the other static data collection
			int taskStageMonsterId = tsm.getId();
			taskStageMonsterIdsToTaskStageMonsters.put(taskStageMonsterId, tsm);

		}
	}

	
	
	
	public void reload() {
		setStaticTaskStageIdsToTaskStageMonster();
	}


	public TaskStageMonsterEntityManager getTaskStageMonsterEntityManager() {
		return taskStageMonsterEntityManager;
	}

	public void setTaskStageMonsterEntityManager(
			TaskStageMonsterEntityManager taskStageMonsterEntityManager) {
		this.taskStageMonsterEntityManager = taskStageMonsterEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
}
