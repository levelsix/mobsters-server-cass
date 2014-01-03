package com.lvl6.mobsters.services.monsterhealingforuser;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingHistory;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface MonsterHealingHistoryService {
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	//RETRIEVING STUFF****************************************************************
	
	
	//INSERTING STUFF****************************************************************
	public abstract void insertHealingHistory(UUID uId, Date timeOfEntry,
			Map<UUID, Integer> prevHps, Map<UUID, MonsterHealingForUser> inHealing,
			Collection<UUID> finishedMfuIds, Map<UUID, MonsterForUser> idsToUserMonsters,
			boolean healingCancelled);
	
	
	//SAVING STUFF****************************************************************
	public abstract void saveUserMonsterHealing(Collection<MonsterHealingHistory> mhfuList);
	
	//UPDATING STUFF****************************************************************
	
	
	//DELETING STUFF****************************************************************
	
	//for the setter dependency injection or something****************************************************************
	public abstract MonsterHealingHistoryEntityManager getMonsterHealingHistoryEntityManager();
	
	public abstract void setMonsterHealingHistoryEntityManager(MonsterHealingHistoryEntityManager monsterHealingHistoryEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}