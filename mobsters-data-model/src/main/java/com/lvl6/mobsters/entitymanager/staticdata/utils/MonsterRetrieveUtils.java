package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.MonsterEntityManager;
import com.lvl6.mobsters.po.staticdata.Monster;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class MonsterRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private Map<Integer, Monster> idsToMonsters;

	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER;

	
	
	@Autowired
	protected MonsterEntityManager monsterEntityManager;
	
	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	
	public Map<Integer, Monster> getMonsterIdsToMonsters() {
		if (null == idsToMonsters) {
			setStaticIdsToMonsters();
		}
		return idsToMonsters;
	}
	

	public Monster getMonsterForMonsterId(Integer id) {
		log.debug("retrieve monster data for id " + id);
		if (null == idsToMonsters) {
			setStaticIdsToMonsters();      
		}
		return idsToMonsters.get(id);
	}

	//maybe make Set into a collection
	public Map<Integer, Monster> getMonsterIdsToMonstersForMonsterIds(Set<Integer> ids) {
		log.debug("retrieve monsters data for ids " + ids);
		if (null == idsToMonsters) {
			setStaticIdsToMonsters();      
		}
		Map<Integer, Monster> toreturn = new HashMap<Integer, Monster>();
		for (Integer id : ids) {
			toreturn.put(id,  idsToMonsters.get(id));
		}
		return toreturn;
	}
	
	public Monster getEvolvedMonster(Monster curMonzter, int monzterId) {
		log.debug("retrieving evolved form of monster:" + curMonzter);
		if (null == idsToMonsters) {
			setStaticIdsToMonsters();
		}
		if (null == curMonzter) {
			curMonzter = getMonsterForMonsterId(monzterId);
		}
		
		int evolvedMonzterId = curMonzter.getEvolutionMonsterId();
		
		Monster evolvedMonzter = null;
		//gotta watch out for monsters with id of 0 or less
		if (evolvedMonzterId > 0 && idsToMonsters.containsKey(evolvedMonzterId)) {
			evolvedMonzter = idsToMonsters.get(evolvedMonzterId);
		}
	
		return evolvedMonzter;
	}

	private  void setStaticIdsToMonsters() {
		log.debug("setting  map of monsterIds to monsters");
		
		//construct the search parameters
		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<Monster> monsterList = getMonsterEntityManager().get().find(cqlquery);
		
		//fill up the map
		idsToMonsters = new HashMap<Integer, Monster>();
		for(Monster m : monsterList) {
			//ensuring that enum string is stripped of white space and capitalized
			String typeStr = m.getMonsterQuality();
			String newTypeStr = typeStr.trim().toUpperCase(Locale.ENGLISH);
			if (!typeStr.equals(newTypeStr)) {
				log.error("monster quality incorrectly set. monster=" + m);
			}
			m.setMonsterQuality(newTypeStr);
			
			typeStr = m.getMonsterElement();
			newTypeStr = typeStr.trim().toUpperCase(Locale.ENGLISH);
			if (!typeStr.equals(newTypeStr)) {
				log.error("monster element incorrectly set. monster=" + m);
			}
			m.setMonsterElement(newTypeStr);
			
			Integer intId = m.getId();
			idsToMonsters.put(intId, m);
		}
	}


	public void reload() {
		setStaticIdsToMonsters();
	}
	
	

	public MonsterEntityManager getMonsterEntityManager() {
		return monsterEntityManager;
	}

	public void setMonsterEntityManager(
			MonsterEntityManager monsterEntityManager) {
		this.monsterEntityManager = monsterEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
