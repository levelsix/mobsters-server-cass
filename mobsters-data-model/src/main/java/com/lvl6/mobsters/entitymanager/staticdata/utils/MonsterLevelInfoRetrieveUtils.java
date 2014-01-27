package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.MonsterLevelInfoEntityManager;
import com.lvl6.mobsters.po.staticdata.MonsterLevelInfo;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class MonsterLevelInfoRetrieveUtils {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private Map<Integer, Map<Integer, MonsterLevelInfo>> monsterIdToLevelToInfo;
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER_LEVEL_INFO;

	@Autowired
	protected MonsterLevelInfoEntityManager monsterLevelInfoEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	

	public Map<Integer, Map<Integer, MonsterLevelInfo>> getMonsterIdToLevelToInfo() {
		log.debug("retrieving all monster lvl info data");
		if (monsterIdToLevelToInfo == null) {
			setStaticMonsterIdToLevelToInfo();
		}
		return monsterIdToLevelToInfo;
	}

	
	public Map<Integer, MonsterLevelInfo> getMonsterLevelInfoForMonsterId(int id) {
		log.debug("retrieving monster lvl info for monster id=" + id);
		if (null == monsterIdToLevelToInfo) {
			setStaticMonsterIdToLevelToInfo();
		}

		if (!monsterIdToLevelToInfo.containsKey(id)) {
			log.error("no monster level info for monsterId=" + id);
		}

		return monsterIdToLevelToInfo.get(id);
	}
	
	
	private  void setStaticMonsterIdToLevelToInfo() {
		log.debug("setting map of monster ids to levels to info");

		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<MonsterLevelInfo> list = getMonsterLevelInfoEntityManager().get().find(cqlquery);
		monsterIdToLevelToInfo = new HashMap<Integer, Map<Integer, MonsterLevelInfo>>();
		for(MonsterLevelInfo mli : list) {
			int monsterId = mli.getMonsterId();
			int lvl = mli.getLvl();
			
			if (!monsterIdToLevelToInfo.containsKey(monsterId)) {
				//base case where have not encountered this monster id before
	        	Map<Integer, MonsterLevelInfo> levelToInfo =
	        			new HashMap<Integer, MonsterLevelInfo>();
	        	monsterIdToLevelToInfo.put(monsterId, levelToInfo);
			}
			
			Map<Integer, MonsterLevelInfo> levelToInfo = monsterIdToLevelToInfo
					.get(monsterId);
			
			levelToInfo.put(lvl, mli);
		}		
	}
	

	public  void reload() {
		setStaticMonsterIdToLevelToInfo();
	}
	
	

	public MonsterLevelInfoEntityManager getMonsterLevelInfoEntityManager() {
		return monsterLevelInfoEntityManager;
	}

	public void setMonsterLevelInfoEntityManager(
			MonsterLevelInfoEntityManager monsterLevelInfoEntityManager) {
		this.monsterLevelInfoEntityManager = monsterLevelInfoEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
