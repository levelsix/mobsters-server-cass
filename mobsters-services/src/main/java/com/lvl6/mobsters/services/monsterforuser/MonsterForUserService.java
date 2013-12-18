package com.lvl6.mobsters.services.monsterforuser;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.staticdata.Monster;
import com.lvl6.mobsters.po.staticdata.TaskStageMonster;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface MonsterForUserService {
	
	//CONTROLLER LOGIC STUFF
	public abstract Map<Integer, Integer> completeMonsterForUserFromMonsterIdsAndQuantities(
			Map<Integer, MonsterForUser> monsterIdToIncompleteUserMonster,
			Map<Integer, Integer> monsterIdToQuantity);
	
	public abstract int completeMonsterForUserFromQuantity(MonsterForUser mfu,
			int numPiecesAvailable, Monster monzter);
	
	public abstract List<MonsterForUser> createMonstersForUserFromQuantities(UUID userId,
			Map<Integer, Integer> monsterIdsToQuantities, Date combineStartTime);
	
	public abstract List<MonsterForUser> createMonsterForUserFromQuantity(UUID userId,
			Monster monzter, int quantity, Date combineStartTime);
	
	public abstract MonsterForUser createMonsterForUser(UUID userId, int monsterId,
			int curExp, int curLvl, int curHealth, int numPieces, boolean isComplete,
			Date combineStartTime, int teamSlotNum, String sourceOfPieces);
	
	public abstract List<UUID> selectWholeButNotCombinedUserMonsters(
			Map<UUID, MonsterForUser> idsToUserMonsters);
	
	public float sumProbabilities(List<TaskStageMonster> taskStageMonsters);
	
	
	
	//RETRIEVING STUFF
	public abstract List<MonsterForUser> getMonstersForUser(UUID userId);
	
	public abstract Map<UUID, List<MonsterForUser>> getUserIdsToMonsterTeamForUserIds(
			List<UUID> userIds);
			
	public abstract MonsterForUser getSpecificUserMonster(UUID userMonsterId);
	
	public abstract Map<UUID, MonsterForUser> getSpecificOrAllUserMonstersForUser(
			UUID userId, Collection<UUID> userMonsterIds);
	
	public abstract Map<Integer, MonsterForUser> getIncompleteMonstersWithUserAndMonsterIds(
			UUID userId, Collection<Integer> monsterIds);
	
	
			
	
	//INSERTING STUFF
	
	//SAVING STUFF****************************************************************
	
	public abstract void saveUserMonsters(List<MonsterForUser> mfuList, Date combineDate,
			String additionalSop);
	
	
	//UPDATING STUFF
	public abstract List<MonsterForUser> updateUserMonstersForUser(UUID userId,
			Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
			Date combineStartDate);
	
	//DELETING STUFF
	public abstract void deleteUserMonster(UUID deleteUserMonsterUuid);
	
	public abstract void deleteUserMonsters(List<UUID> deleteUserMonstersList);
	
	
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
	
	public abstract StructureLab getEquipmentCorrespondingToUserEquip(MonsterForUser ue);
	
	public abstract List<MonsterForUser> getAllEquippedUserEquipsForUser(UUID userId);
	*/
	
}