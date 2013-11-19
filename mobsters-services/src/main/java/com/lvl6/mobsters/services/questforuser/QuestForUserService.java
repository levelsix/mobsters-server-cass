package com.lvl6.mobsters.services.questforuser;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.QuestForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.QuestRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.services.time.TimeUtils;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface QuestForUserService {
		
	//RETRIEVING STUFF
	public abstract Map<Integer, QuestForUser> getQuestIdsToUserQuestsForUser(UUID userId);
	
	public abstract List<Integer> getAvailableQuestIdsForUser(UUID userId);

	public abstract List<Integer> getAvailableQuestIds(List<Integer> redeemedIds,
			List<Integer> inprogressIds);
	
	public abstract QuestForUser getSpecificUnredeemedUserQuest(UUID userId, int questId);
	
	
	
	//INSERTING STUFF
	public abstract QuestForUser createNewUserQuestForUser(UUID userId, int questId, Date now);
	
	
	
	//for the setter dependency injection or something
	public abstract QuestRetrieveUtils getQuestRetrieveUtils();
	
	public abstract void setQuestRetrieveUtils(QuestRetrieveUtils questRetrieveUtils);
	
	public abstract QuestForUserEntityManager getQuestForUserEntityManager();

	public abstract void setQuestForUserEntityManager(
			QuestForUserEntityManager questForUserEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil(); 
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
	public abstract TimeUtils getTimeUtils();
	
	public abstract void setTimeUtils(TimeUtils timeUtils);
}
