package com.lvl6.mobsters.services.monsterforuserdeleted;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserDeletedEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUserDeleted;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface MonsterForUserDeletedService {
	
	//CONTROLLER LOGIC STUFF
	
	
	//RETRIEVING STUFF
	public abstract List<MonsterForUserDeleted> getMonstersForUser(UUID userId);
	
	public abstract Map<UUID, List<MonsterForUserDeleted>> getUserIdsToMonsterTeamForUserIds(
			List<UUID> userIds);
			
	public abstract MonsterForUserDeleted getSpecificUserMonster(UUID userMonsterId);
	
	public abstract Map<UUID, MonsterForUserDeleted> getSpecificOrAllUserMonstersForUser(
			UUID userId, Collection<UUID> userMonsterIds);
	
	public abstract Map<Integer, MonsterForUserDeleted> getIncompleteMonstersWithUserAndMonsterIds(
			UUID userId, Collection<Integer> monsterIds);
	
	
	//INSERTING STUFF
	public abstract void createUserMonsterDeletedFromUserMonsters(String deleteReason,
			Map<UUID, String> details, Date date, Map<UUID, MonsterForUser> mfuMap);
	
	
	
	//SAVING STUFF****************************************************************
	
	public abstract void saveUserMonster(MonsterForUserDeleted mfu);
	
	public abstract void saveUserMonsters(List<MonsterForUserDeleted> mfuList);
	
	
	//UPDATING STUFF
	
	
	//DELETING STUFF
	
	
	
	//for the setter dependency injection or something
	public abstract MonsterForUserDeletedEntityManager getMonsterForUserDeletedEntityManager();
	
	public abstract void setMonsterForUserDeletedEntityManager(MonsterForUserDeletedEntityManager monsterForUserDeletedEntityManager);
	
	public abstract MonsterRetrieveUtils getMonsterRetrieveUtils();
	
	public abstract void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}
