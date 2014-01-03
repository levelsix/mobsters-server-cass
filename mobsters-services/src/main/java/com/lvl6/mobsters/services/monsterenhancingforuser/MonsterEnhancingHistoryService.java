package com.lvl6.mobsters.services.monsterenhancingforuser;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterEnhancingHistoryEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingHistory;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;

public interface MonsterEnhancingHistoryService {
	
	//CONTROLLER LOGIC STUFF
	
	//RETRIEVING STUFF
	
	//INSERTING STUFF
	
	//SAVING STUFF****************************************************************
	public abstract void insertEnhancingHistory(UUID uId, Date timeOfEntry, int prevExp,
			Map<UUID, MonsterEnhancingForUser> inEnhancing, Collection<UUID> finishedMfuIds,
			Map<UUID, MonsterForUser> idsToUserMonsters, UUID enhancingBaseMfuId,
			boolean enhancingCancelled);
	
	public abstract void saveUserMonsters(List<MonsterEnhancingHistory> mfuList);
	
	
	//UPDATING STUFF
	
	
	//DELETING STUFF
	
	
	//for the setter dependency injection or something
	public abstract MonsterEnhancingHistoryEntityManager getMonsterEnhancingHistoryEntityManager();
	
	public abstract void setMonsterEnhancingHistoryEntityManager(MonsterEnhancingHistoryEntityManager monsterEnhancingHistoryEntityManager);
	
	
}