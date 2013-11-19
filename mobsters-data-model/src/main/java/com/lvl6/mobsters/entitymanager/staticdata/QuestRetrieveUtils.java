package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.properties.MobstersDbTables;
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
	
	

	public  Quest getQuestForId(Integer id) {
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

		//don't specify any conditions in the where clause, so using null
		String cqlquery = getQueryConstructionUtil().selectRowsQuery(TABLE_NAME, null, null);
		List<Quest> questList = getQuestEntityManager().get().find(cqlquery);
		
		//fill up the map
		idsToQuests = new HashMap<Integer, Quest>();
		for(Quest q : questList) {
			Integer intId = q.getId();
			
			//convert string into a list
			String questsRequiredForThis = q.getQuestsRequiredForThis();
			List<Integer> requiredLists = getQueryConstructionUtil().explodeIntoInts(
					questsRequiredForThis, delimiter);
			
			//Store it into the quest so we don't have to do this again 
			//(well, at least until the next server restart or when static data is reloaded)
			q.setQuestsRequiredForThisAsList(requiredLists);
			
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
