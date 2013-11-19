package com.lvl6.mobsters.services.questforuser;

import java.util.ArrayList;
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
	
	
	
	@Override
	public  Map<Integer, QuestForUser> getQuestIdsToUserQuestsForUser(UUID userId) {
		//construct the search parameters
		List<String> keys = new ArrayList<String>();
		keys.add(MobstersDbTables.QUEST_FOR_USER___USER_ID);
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.QUEST_FOR_USER___USER_ID, userId.toString());
		
		//query db
		String cqlquery = getQueryConstructionUtil().selectRowsQuery(TABLE_NAME, equalityConditions, keys); 
		List <QuestForUser> qfuList = getQuestForUserEntityManager().get().find(cqlquery);
		
		Map<Integer, QuestForUser> questIdsToUserQuests = new HashMap<Integer, QuestForUser>();
		for(QuestForUser qfu : qfuList) {
			
			Integer questId = qfu.getQuestId();
			questIdsToUserQuests.put(questId, qfu);
		}
		return questIdsToUserQuests;
	}

	@Override
	public List<Integer> getAvailableQuestIds(List<Integer> redeemedIds,
			List<Integer> inprogressIds) {
		QuestGraph qg = getQuestRetrieveUtils().getQuestGraph();
		return qg.getQuestsAvailable(redeemedIds, inprogressIds);
	}
	
	
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
	
}
