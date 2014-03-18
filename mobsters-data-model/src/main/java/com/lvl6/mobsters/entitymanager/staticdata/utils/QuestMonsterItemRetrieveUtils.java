package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.QuestMonsterItemEntityManager;
import com.lvl6.mobsters.po.staticdata.QuestMonsterItem;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class QuestMonsterItemRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private Map<Integer, Map<Integer, QuestMonsterItem>> questIdsToMonsterIdsToItems;

	private final String TABLE_NAME = MobstersDbTables.TABLE_QUEST_MONSTER_ITEM;
	
	@Autowired
	protected QuestMonsterItemEntityManager questMonsterItemEntityManager;
	
	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	
	public Map<Integer, Map<Integer, QuestMonsterItem>> getQuestIdsToMonsterIdsToItems() {
		log.debug("retrieving all task stage data map");
		if (questIdsToMonsterIdsToItems == null) {
			setStaticQuestIdsToMonsterIdsToItems();
		}
		return questIdsToMonsterIdsToItems;
	}

	public QuestMonsterItem getItemForQuestAndMonsterId(int questId, int monsterId) {
		if (questIdsToMonsterIdsToItems == null) {
			setStaticQuestIdsToMonsterIdsToItems();      
		}

		if (!questIdsToMonsterIdsToItems.containsKey(questId)) {
			log.warn("no items for questId=" + questId);
			return null; 
		}

		Map<Integer, QuestMonsterItem> monsterIdsToItems =
				questIdsToMonsterIdsToItems.get(questId);

		if (monsterIdsToItems.containsKey(monsterId)) {
			return monsterIdsToItems.get(monsterId);

		} else {
			log.warn("no items for questId=" + questId + " monsterId=" + monsterId);
		}
		return null;

	}

	public Map<Integer, QuestMonsterItem> getItemsForQuestId(int questId) {
		log.debug("retrieve quest_monster_item data for questId " + questId);
		if (questIdsToMonsterIdsToItems == null) {
			setStaticQuestIdsToMonsterIdsToItems();      
		}
		return questIdsToMonsterIdsToItems.get(questId);
	}

	
	
	private void setStaticQuestIdsToMonsterIdsToItems() {
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
		List<QuestMonsterItem> questMonsterItemList = getQuestMonsterItemEntityManager().get().find(cqlquery);
		
		//fill up the maps
		questIdsToMonsterIdsToItems = new HashMap<Integer, Map<Integer, QuestMonsterItem>>();
		for(QuestMonsterItem qmi : questMonsterItemList) {
			//store in quest to monster to item map
			int questId = qmi.getQuestId();
			

			//base case, no key with quest id exists, so create map with
	        //key: quest id, to value: another map
	        if (!questIdsToMonsterIdsToItems.containsKey(questId)) {
	          questIdsToMonsterIdsToItems.put(questId, new HashMap<Integer, QuestMonsterItem>());
	        }
	        
	        //stick item into the map of monster ids to QuestMonsterItem objects
	        Map<Integer, QuestMonsterItem> monsterIdsToItems =
	        		questIdsToMonsterIdsToItems.get(questId);

	        int monsterId = qmi.getMonsterId();
	        monsterIdsToItems.put(monsterId, qmi);
		}
	}

	public void reload() {
		setStaticQuestIdsToMonsterIdsToItems();
	}
	
	

	public QuestMonsterItemEntityManager getQuestMonsterItemEntityManager() {
		return questMonsterItemEntityManager;
	}

	public void setQuestMonsterItemEntityManager(
			QuestMonsterItemEntityManager questMonsterItemEntityManager) {
		this.questMonsterItemEntityManager = questMonsterItemEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
