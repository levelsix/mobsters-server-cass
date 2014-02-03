package com.lvl6.mobsters.services.monsterevolvingforuser;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterEvolvingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEvolvingForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface MonsterEvolvingForUserService {
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	
	//RETRIEVING STUFF****************************************************************
	public abstract Map<UUID, MonsterEvolvingForUser> getCatalystIdsToEvolutionsForUser(UUID userId);
	
	public abstract MonsterEvolvingForUser getEvolutionForUser(UUID userId);
	
	
	//INSERTING STUFF****************************************************************
	public abstract void insertIntoMonsterEvolvingForUser(UUID userId, Date startTime,
			UUID catalystUserMonsterId, List<UUID> userMonsterIds);
	
	//SAVING STUFF****************************************************************
	
	public abstract void saveUserMonsters(List<MonsterEvolvingForUser> mfuList);
	
	
	//UPDATING STUFF****************************************************************
//	public abstract List<MonsterEvolvingForUser> updateUserMonstersEvolvingForUser(UUID userId,
//			Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
//			Date combineStartDate);
	
	//DELETING STUFF****************************************************************
	public abstract void deleteUserMonsterEvolving(UUID deleteUserMonsterEvolvingUuid);
	
	public abstract void deleteUserMonstersEvolving(List<UUID> deleteUserMonstersList);
	
	
	//for the setter dependency injection or something
	public abstract MonsterEvolvingForUserEntityManager getMonsterEvolvingForUserEntityManager();
	
	public abstract void setMonsterEvolvingForUserEntityManager(MonsterEvolvingForUserEntityManager monsterEvolvingForUserEntityManager);
	
	public abstract MonsterRetrieveUtils getMonsterRetrieveUtils();
	
	public abstract void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}