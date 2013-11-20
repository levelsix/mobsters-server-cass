package com.lvl6.mobsters.services.monsterforuser;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface MonsterForUserService {
	
	//RETRIEVING STUFF
	public abstract List<MonsterForUser> getMonstersForUser(UUID userId);
	
	public abstract Map<UUID, List<MonsterForUser>> getUserIdsToMonsterTeamForUserIds(
			List<UUID> userIds);
			
	public abstract MonsterForUser getSpecificUserMonster(UUID userMonsterId);
	
	public abstract Map<UUID, MonsterForUser> getSpecificOrAllUserMonstersForUser(
			UUID userId, Collection<UUID> userMonsterIds);
	
	public abstract Map<Integer, MonsterForUser> getIncompleteMonstersWithUserAndMonsterIds(
			int userId, Collection<Integer> monsterIds);
	
	
			
	
	//INSERTING STUFF
	
	
	//for the setter dependency injection or something
	public abstract MonsterForUserEntityManager getMonsterForUserEntityManager();
	
	public abstract void setMonsterForUserEntityManager(MonsterForUserEntityManager monsterForUserEntityManager);
	
	public abstract MonsterRetrieveUtils getMonsterRetrieveUtils();
	
	public abstract void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
	/*
	//OLD AOC2 STUFF
	public abstract Map<UUID, MonsterForUser> getUserEquipsByUserEquipIds (Collection<UUID> ids);
	
	public abstract void saveEquips(Collection<MonsterForUser> newEquips);
	
	public abstract void getEquippedUserEquips(List<MonsterForUser> allUserEquips, List<MonsterForUser> equippedUserEquips);
	
	
	public abstract MonsterForUser getUserEquipForId(UUID id);
	
	public abstract Map<UUID, MonsterForUser> getUserEquipsForIds(List<UUID> ids);
	
	public abstract List<MonsterForUser> getAllUserEquipsForUser(UUID userId);
	
	public abstract Equipment getEquipmentCorrespondingToUserEquip(MonsterForUser ue);
	
	public abstract List<MonsterForUser> getAllEquippedUserEquipsForUser(UUID userId);
	*/
	
}