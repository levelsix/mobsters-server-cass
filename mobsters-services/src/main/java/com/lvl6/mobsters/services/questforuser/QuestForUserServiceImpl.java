package com.lvl6.mobsters.services.questforuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.QuestForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.QuestRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.services.time.TimeUtils;
import com.lvl6.mobsters.utils.QueryConstructionUtil;
import com.lvl6.mobsters.utils.QuestGraph;

@Component
public class QuestForUserServiceImpl implements QuestForUserService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_QUEST_FOR_USER;
	  
	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils;
	
	@Autowired
	protected QuestForUserEntityManager questForUserEntityManager;

	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	private TimeUtils timeUtils;
	
	
	//RETRIEVING STUFF
	@Override
	public  Map<Integer, QuestForUser> getQuestIdsToUserQuestsForUser(UUID userId) {
		//construct the search parameters
		List<String> keys = new ArrayList<String>();
		keys.add(MobstersDbTables.QUEST_FOR_USER___USER_ID);
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.QUEST_FOR_USER___USER_ID, userId.toString());
		
		//query db
		String cqlquery = getQueryConstructionUtil().selectRowsQuery(TABLE_NAME, equalityConditions, keys); 
		List<QuestForUser> qfuList = getQuestForUserEntityManager().get().find(cqlquery);
		
		Map<Integer, QuestForUser> questIdsToUserQuests = new HashMap<Integer, QuestForUser>();
		for(QuestForUser qfu : qfuList) {
			
			Integer questId = qfu.getQuestId();
			questIdsToUserQuests.put(questId, qfu);
		}
		return questIdsToUserQuests;
	}
	
	@Override
	public List<Integer> getAvailableQuestIdsForUser(UUID userId) {
		Map<Integer, QuestForUser> questIdsToUserQuests = getQuestIdsToUserQuestsForUser(userId);
	    Collection<QuestForUser> inProgressAndRedeemedUserQuests = questIdsToUserQuests.values();
	    List<Integer> inProgressQuestIds = new ArrayList<Integer>();
	    List<Integer> redeemedQuestIds = new ArrayList<Integer>();
	    
	    List<Integer> availableQuestIds = null;
	    
	    if (inProgressAndRedeemedUserQuests != null) {
	    	for (QuestForUser uq : inProgressAndRedeemedUserQuests) {
	    		if (uq.isRedeemed()) {
	    			redeemedQuestIds.add(uq.getQuestId());
	    		} else {
	    			inProgressQuestIds.add(uq.getQuestId());  
	    		}
	    	}
	    	availableQuestIds = getAvailableQuestIds(redeemedQuestIds, inProgressQuestIds);
	    }
	    return availableQuestIds;
	}

	@Override
	public List<Integer> getAvailableQuestIds(List<Integer> redeemedIds,
			List<Integer> inprogressIds) {
		QuestGraph qg = getQuestRetrieveUtils().getQuestGraph();
		return qg.getQuestsAvailable(redeemedIds, inprogressIds);
	}
	
	@Override
	public QuestForUser getSpecificUnredeemedUserQuest(UUID userId, int questId) {
		//construct the search parameters
		List<String> keys = new ArrayList<String>();
		keys.add(MobstersDbTables.QUEST_FOR_USER___USER_ID);
		keys.add(MobstersDbTables.QUEST_FOR_USER__QUEST_ID);
		keys.add(MobstersDbTables.QUEST_FOR_USER__IS_REDEEMED);
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.QUEST_FOR_USER___USER_ID, userId);
		equalityConditions.put(MobstersDbTables.QUEST_FOR_USER__QUEST_ID, questId);
		equalityConditions.put(MobstersDbTables.QUEST_FOR_USER__IS_REDEEMED, false);
		
		//query db
		String cqlQuery = getQueryConstructionUtil().selectRowsQuery(TABLE_NAME, equalityConditions, keys);
		List<QuestForUser> qfuList = getQuestForUserEntityManager().get().find(cqlQuery);
		
		if (null == qfuList || qfuList.isEmpty()) {
			log.warn("no unredeemed QuestForUser exists for userId=" + userId +
					"\t questId=" + questId);
			return null;
		} else if (qfuList.size() > 1) {
			log.warn("multiple QuestForUser exist for userId=" + userId + "\t questId=" +
					questId + "\t quests=" + qfuList + "\t keeping most recent");
			
			return getMostRecentQuestForUser(qfuList);
		} else{
			QuestForUser qfu = qfuList.get(0);
			log.info("retrieved one QuestForUser. qfu=" + qfu);
			return qfu;
		}
	}
	
	private QuestForUser getMostRecentQuestForUser(List<QuestForUser> qfuList) {
		QuestForUser mostRecent = qfuList.get(0);
		for (int i = 1; i < qfuList.size(); i++) {
			QuestForUser prospectiveMostRecent = qfuList.get(i);
			Date pmrDate = prospectiveMostRecent.getTimeAccepted();
			
			Date mostRecentDate = mostRecent.getTimeAccepted();
			
			if (getTimeUtils().isFirstEarlierThanSecond(mostRecentDate, pmrDate)) {
				//want most recent so pmrDate is newer than existing
				mostRecent = prospectiveMostRecent;
			}
		}
		
		return mostRecent;
	}
	
	
	
	//INSERTING STUFF
	@Override
	public QuestForUser createNewUserQuestForUser(UUID userId, int questId, Date now) {
		QuestForUser qfu = new QuestForUser();
		qfu.setUserId(userId);
		qfu.setQuestId(questId);
		qfu.setTimeAccepted(now);
		//the other columns are default values for new QuestForUser
		
		log.info("saving new quest for user. qfu=" + qfu);
		getQuestForUserEntityManager().get().put(qfu);
		
		return qfu;
	}
	
	
	
	
	@Override
	public QuestRetrieveUtils getQuestRetrieveUtils() {
		return questRetrieveUtils;
	}
	@Override
	public void setQuestRetrieveUtils(QuestRetrieveUtils questRetrieveUtils) {
		this.questRetrieveUtils = questRetrieveUtils;
	}

	@Override
	public QuestForUserEntityManager getQuestForUserEntityManager() {
		return questForUserEntityManager;
	}
	@Override
	public void setQuestForUserEntityManager(
			QuestForUserEntityManager questForUserEntityManager) {
		this.questForUserEntityManager = questForUserEntityManager;
	}
	@Override
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	@Override
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	@Override
	public TimeUtils getTimeUtils() {
		return timeUtils;
	}
	@Override
	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}
	
	
}
