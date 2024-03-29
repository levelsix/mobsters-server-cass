package com.lvl6.mobsters.services.monsterforuser;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterLevelInfoRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEvolvingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.staticdata.Monster;
import com.lvl6.mobsters.po.staticdata.TaskStageMonster;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface MonsterForUserService {
	
	//CONTROLLER LOGIC STUFF****************************************************************
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
	
	public abstract List<MonsterForUser> replaceBattleTeamSlot(int oldTeamSlotNum,
			int newTeamSlotNum, Map<UUID, MonsterForUser> idsToUserMonsters);
	
	public abstract MonsterForUser createEvolvedMonster(UUID userId, MonsterForUser mfu,
			MonsterEvolvingForUser mefu, Date combineStartTime, String sourceOfPieces);
	
	//RETRIEVING STUFF****************************************************************
	public abstract List<MonsterForUser> getMonstersForUser(UUID userId);
	
	public abstract Map<UUID, List<MonsterForUser>> getUserIdsToMonsterTeamForUserIds(
			List<UUID> userIds);
			
	public abstract MonsterForUser getSpecificUserMonster(UUID userMonsterId);
	
	public abstract Map<UUID, MonsterForUser> getSpecificOrAllUserMonstersForUser(
			UUID userId, Collection<UUID> userMonsterIds);
	
	public abstract Map<Integer, MonsterForUser> getIncompleteMonstersWithUserAndMonsterIds(
			UUID userId, Collection<Integer> monsterIds);
	
	public abstract Map<UUID, MonsterForUser> getUserMonstersInEvolution(UUID userId,
			MonsterEvolvingForUser mefu);
			
	
	//INSERTING STUFF****************************************************************
	
	//SAVING STUFF****************************************************************
	
	public abstract void saveUserMonster(MonsterForUser mfu);
	
	public abstract void saveUserMonsters(Collection<MonsterForUser> mfuList);
	
	public abstract void saveUserMonsters(Collection<MonsterForUser> mfuList, Date combineDate,
			String additionalSop);
	
	
	//UPDATING STUFF****************************************************************
	public abstract List<MonsterForUser> updateUserMonstersForUser(UUID userId,
			Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
			Date combineStartDate);
	
	public abstract void updateUserMonstersHealths(Map<UUID, Integer> userMonsterIdToExpectedHealth,
			Map<UUID, MonsterForUser> existingUserMonsters);
	
	public abstract void updateBattleTeamSlot(int newTeamSlotNum, MonsterForUser mfu);
	
	public abstract void updateCompleteUserMonsters(List<UUID> mfuIdList,
			Map<UUID, MonsterForUser> idsToUserMonsters);
	
	//DELETING STUFF****************************************************************
	public abstract void deleteUserMonster(UUID deleteUserMonsterUuid);
	
	public abstract void deleteUserMonsters(List<UUID> deleteUserMonstersList);
	
	
	//for the setter dependency injection or something
	public abstract MonsterForUserEntityManager getMonsterForUserEntityManager();
	
	public abstract void setMonsterForUserEntityManager(MonsterForUserEntityManager monsterForUserEntityManager);
	
	public abstract MonsterRetrieveUtils getMonsterRetrieveUtils();
	
	public abstract void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils);

	public abstract QueryConstructionUtil getQueryConstructionUtil();
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils();

	public void setMonsterLevelInfoRetrieveUtils(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils);	
}