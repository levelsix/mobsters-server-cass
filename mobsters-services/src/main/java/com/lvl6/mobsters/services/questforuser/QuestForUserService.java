package com.lvl6.mobsters.services.questforuser;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.QuestForUserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface QuestForUserService {
		
	public abstract Map<Integer, QuestForUser> getQuestIdsToUserQuestsForUser(UUID userId);

	public abstract List<Integer> getAvailableQuestIds(List<Integer> redeemedIds,
			List<Integer> inprogressIds);
	
	
	
	//for the setter dependency injection or something
	public abstract QuestForUserEntityManager getQuestForUserEntityManager();

	public abstract void setQuestForUserEntityManager(
			QuestForUserEntityManager questForUserEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil(); 
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
}
