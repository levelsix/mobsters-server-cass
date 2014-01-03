package com.lvl6.mobsters.services.monsterhealingforuser;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface MonsterHealingForUserService {
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	//RETRIEVING STUFF****************************************************************
	public abstract Map<UUID, MonsterHealingForUser> getMonstersHealingForUser(UUID userId);
	
//	public abstract MonsterHealingForUser getSpecificUserMonster(UUID userMonsterId);
	
	public abstract Map<UUID, MonsterHealingForUser> getSpecificOrAllUserMonstersHealingForUser(
			UUID userId, Collection<UUID> userMonsterIds);
	
	
	//INSERTING STUFF****************************************************************
	
	//SAVING STUFF****************************************************************
	public abstract void saveUserMonsterHealing(Collection<MonsterHealingForUser> mhfuList);
	
	//UPDATING STUFF****************************************************************
	public abstract void healUserMonsters(Map<UUID, Integer> userMonsterIdsToHealths,
			Map<UUID, MonsterForUser> idsToUserMonsters);
	
	//DELETING STUFF****************************************************************
	public abstract void deleteUserMonsterHealing(UUID deleteUserMonsterUuid);
	
	public abstract void deleteUserMonstersHealing(Collection<UUID> deleteUserMonstersList);
	
	
	//for the setter dependency injection or something****************************************************************
	public abstract MonsterHealingForUserEntityManager getMonsterHealingForUserEntityManager();
	
	public abstract void setMonsterHealingForUserEntityManager(MonsterHealingForUserEntityManager monsterHealingForUserEntityManager);
	
	public abstract MonsterRetrieveUtils getMonsterRetrieveUtils();
	
	public abstract void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
	public abstract MonsterForUserService getMonsterForUserService();

	public abstract void setMonsterForUserService(MonsterForUserService monsterForUserService);
	
}