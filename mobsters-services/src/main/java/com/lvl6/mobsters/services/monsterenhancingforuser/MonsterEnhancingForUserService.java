package com.lvl6.mobsters.services.monsterenhancingforuser;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterEnhancingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface MonsterEnhancingForUserService {
	
	//CONTROLLER LOGIC STUFF
	public abstract UUID selectBaseEnhancingMonsterId(Map<UUID, MonsterEnhancingForUser> mefuIdToMefu);
	
	//RETRIEVING STUFF
	public abstract Map<UUID, MonsterEnhancingForUser> getMonstersEnhancingForUser(UUID userId);
	
//	public abstract MonsterEnhancingForUser getSpecificUserMonster(UUID userMonsterId);
	
	public abstract Map<UUID, MonsterEnhancingForUser> getSpecificOrAllUserMonstersEnhancingForUser(
			UUID userId, Collection<UUID> userMonsterIds);
	
	
	//INSERTING STUFF
	
	//SAVING STUFF****************************************************************
	
	public abstract void saveUserMonsters(List<MonsterEnhancingForUser> mfuList);
	
	
	//UPDATING STUFF
//	public abstract List<MonsterEnhancingForUser> updateUserMonstersEnhancingForUser(UUID userId,
//			Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
//			Date combineStartDate);
	
	//DELETING STUFF
	public abstract void deleteUserMonsterEnhancing(UUID deleteUserMonsterUuid);
	
	public abstract void deleteUserMonstersEnhancing(List<UUID> deleteUserMonstersList);
	
	
	//for the setter dependency injection or something
	public abstract MonsterEnhancingForUserEntityManager getMonsterEnhancingForUserEntityManager();
	
	public abstract void setMonsterEnhancingForUserEntityManager(MonsterEnhancingForUserEntityManager monsterEnhancingForUserEntityManager);
	
	public abstract MonsterRetrieveUtils getMonsterRetrieveUtils();
	
	public abstract void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}