package com.lvl6.mobsters.services.monsterhealingforuser;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface MonsterHealingForUserService {
	
	//CONTROLLER LOGIC STUFF
	
	//RETRIEVING STUFF
	public abstract Map<UUID, MonsterHealingForUser> getMonstersHealingForUser(UUID userId);
	
//	public abstract MonsterHealingForUser getSpecificUserMonster(UUID userMonsterId);
	
	public abstract Map<UUID, MonsterHealingForUser> getSpecificOrAllUserMonstersHealingForUser(
			UUID userId, Collection<UUID> userMonsterIds);
	
	
	//INSERTING STUFF
	
	//SAVING STUFF****************************************************************
	
	public abstract void saveUserMonsters(List<MonsterHealingForUser> mfuList, Date combineDate,
			String additionalSop);
	
	
	//UPDATING STUFF
//	public abstract List<MonsterHealingForUser> updateUserMonstersHealingForUser(UUID userId,
//			Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
//			Date combineStartDate);
	
	//DELETING STUFF
	public abstract void deleteUserMonster(UUID deleteUserMonsterUuid);
	
	public abstract void deleteUserMonsters(List<UUID> deleteUserMonstersList);
	
	
	//for the setter dependency injection or something
	public abstract MonsterHealingForUserEntityManager getMonsterHealingForUserEntityManager();
	
	public abstract void setMonsterHealingForUserEntityManager(MonsterHealingForUserEntityManager monsterHealingForUserEntityManager);
	
	public abstract MonsterRetrieveUtils getMonsterRetrieveUtils();
	
	public abstract void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}