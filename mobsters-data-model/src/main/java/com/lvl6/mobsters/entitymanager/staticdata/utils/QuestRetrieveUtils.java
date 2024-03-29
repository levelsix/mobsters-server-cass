package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.QuestEntityManager;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.Dialogue;
import com.lvl6.mobsters.utils.QueryConstructionUtil;
import com.lvl6.mobsters.utils.QuestGraph;

@Component public class QuestRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private Map<Integer, Quest> idsToQuests;
	private QuestGraph questGraph;

	private final String TABLE_NAME = MobstersDbTables.TABLE_QUEST;

	
	
	@Autowired
	protected QuestEntityManager questEntityManager;
	
	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	
	public Map<Integer, Quest> getQuestIdsToQuests() {
		if (null == idsToQuests) {
			setStaticIdsToQuests();
		}
		return idsToQuests;
	}
	

	public Quest getQuestForId(Integer id) {
		log.debug("retrieve quest data for id " + id);
		if (idsToQuests == null) {
			setStaticIdsToQuests();      
		}
		return idsToQuests.get(id);
	}

	public  Map<Integer, Quest> getQuestsForIds(List<Integer> ids) {
		log.debug("retrieve quests data for ids " + ids);
		if (idsToQuests == null) {
			setStaticIdsToQuests();      
		}
		Map<Integer, Quest> toreturn = new HashMap<Integer, Quest>();
		for (Integer id : ids) {
			toreturn.put(id,  idsToQuests.get(id));
		}
		return toreturn;
	}
	
	public QuestGraph getQuestGraph() {
		log.debug("retrieving quest graph");
	    if (questGraph == null) {
	      setQuestGraph();
	    }
	    return questGraph;
	}

	private  void setStaticIdsToQuests() {
		log.debug("setting  map of questIds to quests");
		String delimiter = ", ";

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
		List<Quest> questList = getQuestEntityManager().get().find(cqlquery);
		
		//fill up the map
		idsToQuests = new HashMap<Integer, Quest>();
		for(Quest q : questList) {
			
			//convert accept dialogue string into Dialogue obj
			Dialogue d = new Dialogue();
			d.setDialogue(q.getAcceptDialogue());
			q.setDialogue(d);
			
			//convert string into a list
			String questsRequiredForThis = q.getQuestsRequiredForThis();
			List<Integer> requiredList = getQueryConstructionUtil().explodeIntoInts(
					questsRequiredForThis, delimiter);
			Set<Integer> requiredSet = new HashSet<Integer>(requiredList);
			
			//Store it into the quest so we don't have to do this again 
			//(well, at least until the next server restart or when static data is reloaded)
			q.setQuestsRequiredForThisAsSet(requiredSet);
			
			//ensuring that enum string is stripped of white space and capitalized
			String typeStr = q.getQuestType();
			String newTypeStr = typeStr.trim().toUpperCase(Locale.ENGLISH);
			if (!typeStr.equals(newTypeStr)) {
				log.error("quest type incorrectly set. quest=" + q);
			}
			q.setQuestType(newTypeStr);
			
			Integer intId = q.getId();
			idsToQuests.put(intId, q);
		}
	}

	private void setQuestGraph() {
		if (null == idsToQuests) {
			setStaticIdsToQuests();      
		}
		Collection<Quest> quests = idsToQuests.values();
		QuestGraph tmp = new QuestGraph(quests);
		questGraph = tmp;
	}


	public void reload() {
		setStaticIdsToQuests();
		setQuestGraph();
	}
	
	

	public QuestEntityManager getQuestEntityManager() {
		return questEntityManager;
	}

	public void setQuestEntityManager(
			QuestEntityManager questEntityManager) {
		this.questEntityManager = questEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
