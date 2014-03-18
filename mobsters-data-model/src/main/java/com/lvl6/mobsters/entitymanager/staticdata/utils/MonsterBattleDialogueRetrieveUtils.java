package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.MonsterBattleDialogueEntityManager;
import com.lvl6.mobsters.po.staticdata.MonsterBattleDialogue;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class MonsterBattleDialogueRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private Map<Integer, List<MonsterBattleDialogue>> monsterIdsToBattleDialogues;

	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER_BATTLE_DIALOGUE;
	
	@Autowired
	protected MonsterBattleDialogueEntityManager monsterBattleDialogueEntityManager;
	
	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	

	public Map<Integer, List<MonsterBattleDialogue>> getMonsterIdToBattleDialogue() {
		log.debug("retrieving all cities data");
		if (monsterIdsToBattleDialogues == null) {
			setStaticMonsterIdToMonsterBattleDialogue();
		}
		return monsterIdsToBattleDialogues;
	}
	
	private void setStaticMonsterIdToMonsterBattleDialogue() {
		log.debug("setting  map of monster ids to dialogues");

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
		List<MonsterBattleDialogue> allDialogue = getMonsterBattleDialogueEntityManager()
				.get().find(cqlquery);
		
		//fill up the maps
		monsterIdsToBattleDialogues = new HashMap<Integer, List<MonsterBattleDialogue>>();
		for(MonsterBattleDialogue aMonsterBattleDialogue : allDialogue) {
			//store in task stage map
			int monsterId = aMonsterBattleDialogue.getMonsterId();
			
			if (!monsterIdsToBattleDialogues.containsKey(monsterId)) {
				//base case where haven't seen this monster yet
				monsterIdsToBattleDialogues.put(monsterId, new ArrayList<MonsterBattleDialogue>());
			}
			
			List<MonsterBattleDialogue> dialogueList = monsterIdsToBattleDialogues.get(monsterId);
			dialogueList.add(aMonsterBattleDialogue);
			
		}
	}

	public void reload() {
		setStaticMonsterIdToMonsterBattleDialogue();
	}
	
	
	public MonsterBattleDialogueEntityManager getMonsterBattleDialogueEntityManager() {
		return monsterBattleDialogueEntityManager;
	}

	public void setMonsterBattleDialogueEntityManager(
			MonsterBattleDialogueEntityManager monsterBattleDialogueEntityManager) {
		this.monsterBattleDialogueEntityManager = monsterBattleDialogueEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
