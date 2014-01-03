package com.lvl6.mobsters.services.monsterforuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.staticdata.Monster;
import com.lvl6.mobsters.po.staticdata.TaskStageMonster;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class MonsterForUserServiceImpl implements MonsterForUserService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER_FOR_USER;
	
	@Autowired
	protected MonsterForUserEntityManager monsterForUserEntityManager;
	
	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	// ASSUMPTION: WHATEVER MONSTER ID EXISTS IN  monsterIdToIncompleteUserMonster
	// THERE IS A CORRESPONDING ENTRY IN monsterIdToQuantity
	// returns the remaining quantities for each monster id after "completing"
	// a user_monster 
	// ALSO MODIFIES the monsters in monsterIdToIncompleteUserMonster
	//monsterIdToQuantity will not be modified
	@Override
	public Map<Integer, Integer> completeMonsterForUserFromMonsterIdsAndQuantities(
			Map<Integer, MonsterForUser> monsterIdToIncompleteUserMonster,
			Map<Integer, Integer> monsterIdToQuantity) {

		Map<Integer, Integer> monsterIdToNewQuantity = new HashMap<Integer, Integer>();
		//IF NO INCOMLETE MonsterForUser EXISTS, THEN METHOD JUST
		//RETURNS A COPY OF THE MAP monsterIdToQuantity
		if (monsterIdToIncompleteUserMonster.isEmpty()) {
			monsterIdToNewQuantity.putAll(monsterIdToQuantity);
			return monsterIdToNewQuantity;
		}

		//retrieve the monsters so we know how much is needed to complete it
		Set<Integer> incompleteMonsterIds = monsterIdToIncompleteUserMonster.keySet();
		Map<Integer, Monster> monsterIdsToMonsters = getMonsterRetrieveUtils()
				.getMonsterIdsToMonstersForMonsterIds(incompleteMonsterIds);


		//for each incomplete user monster, try to complete it with the 
		//available quantity in the monsterIdToQuantity map,
		//monsters in monsterIdToIncompleteUserMonster will be modified
		for (int monsterId : incompleteMonsterIds) {
			MonsterForUser incompleteMfu = monsterIdToIncompleteUserMonster.get(monsterId);
			int numPiecesAvailable = monsterIdToQuantity.get(monsterId);
			Monster monzter = monsterIdsToMonsters.get(monsterId);

			//modify the incompleteMfu
			int newPiecesAvailable = completeMonsterForUserFromQuantity(
					incompleteMfu, numPiecesAvailable, monzter);

			//if any pieces are left, store it for later user
			if (newPiecesAvailable > 0) {
				monsterIdToNewQuantity.put(monsterId, newPiecesAvailable);
			}
		}

		return monsterIdToNewQuantity;
	}
	
	//MonsterForUser mfu will be modified: if mfu has all necessary pieces and
	//num minutes to combine pieces is 0, mfu.isComplete will be set to true
	//returns the number of pieces remaining after using up the pieces
	//available in order to try completing the monster_for_user
	@Override
	public int completeMonsterForUserFromQuantity(MonsterForUser mfu,
			int numPiecesAvailable, Monster monzter) {
		int numPiecesRemaining = 0;

		int numPiecesForCompletion = monzter.getNumPuzzlePieces();
		int existingNumPieces = mfu.getNumPieces();

		int newExistingPieces = existingNumPieces + numPiecesAvailable;

		//two scenarios: 
		//1) there are pieces remaining after trying to complete monsterForUser
		//2) no pieces remaining after trying to complete monsterForUser
		if (newExistingPieces > numPiecesForCompletion) {
			//there are more than enough pieces to complete this monster
			//"complete" the monsterForUser
			mfu.setNumPieces(numPiecesForCompletion);

			//calculate the remaining pieces remaining
			numPiecesRemaining = newExistingPieces - numPiecesForCompletion;

		} else {
			//no extra pieces remaining after trying to complete monsterForUser
			//either sum(numPiecesAvailable, existingNumPieces) less than or equal to
			//numPiecesForCompletion
			mfu.setNumPieces(newExistingPieces);
			//by default numPiecesRemaining is 0
		}

		//if monster for user has enough pieces to be complete, and minutes to combine
		//is 0, it should be set as complete
		int mfuNewNumPieces = mfu.getNumPieces();
		int numMinutesForCompletion = monzter.getMinutesToCombinePieces();
		if (mfuNewNumPieces == numPiecesForCompletion && 0 == numMinutesForCompletion) {
			mfu.setComplete(true);
		}

		return numPiecesRemaining;
	}

	@Override
	public List<MonsterForUser> createMonstersForUserFromQuantities(UUID userId,
			Map<Integer, Integer> monsterIdsToQuantities, Date combineStartTime) {
		List<MonsterForUser> returnList = new ArrayList<MonsterForUser>();

		if(monsterIdsToQuantities.isEmpty()) {
			return returnList;
		}

		Set<Integer> monsterIds = monsterIdsToQuantities.keySet();
		Map<Integer, Monster> monsterIdsToMonsters =  getMonsterRetrieveUtils()
				.getMonsterIdsToMonstersForMonsterIds(monsterIds);

		//for each monster and quantity, create as many complete and incomplete
		//user monsters
		for (int monsterId : monsterIds) {
			Monster monzter = monsterIdsToMonsters.get(monsterId);
			int quantity = monsterIdsToQuantities.get(monsterId);

			log.info("for monsterId=" + monsterId + "\t and for quantity=" + quantity +
					"\t creating some number of a specific monster for a user. monster=" +
					monzter);

			List<MonsterForUser> newUserMonsters = createMonsterForUserFromQuantity(
					userId, monzter, quantity, combineStartTime);
			log.info("some amount of a certain monster created. monster(s)=" +
					newUserMonsters);

			returnList.addAll(newUserMonsters);
		}

		return returnList;
	}
	
	//for A GIVEN MONSTER and QUANTITY of pieces, create as many of this monster as possible
	//THE ID PROPERTY FOR ALL these monsterForUser will be a useless value, say 0
	@Override
	public List<MonsterForUser> createMonsterForUserFromQuantity(UUID userId,
			Monster monzter, int quantity, Date combineStartTime) {
		List<MonsterForUser> returnList = new ArrayList<MonsterForUser>();

		int numPiecesForCompletion = monzter.getNumPuzzlePieces();

		//default values for creating a monster for user
		int monsterId = monzter.getId();
		int currentExp = 0; //not sure if this is right
		int currentLvl = 1; //not sure if this is right
		int currentHealth = monzter.getBaseHp();
		int teamSlotNum = 0;
		String sourceOfPieces = "";

		//decrement quantity by number_of_pieces_to_create_a_monster and
		//this represents one monster_for_user
		for (; quantity > 0; quantity -= numPiecesForCompletion) {
			boolean isComplete = false;
			int numPieces = 0;

			if (quantity >= numPiecesForCompletion) {
				numPieces = numPiecesForCompletion;

				//since there's enough pieces to create a whole monster, if the time
				//it takes to combine a monster is 0 then the monster is complete
				if (monzter.getMinutesToCombinePieces() == 0) {
					isComplete = true;
				}

			} else {
				//this happens only when there isn't enough pieces left to make a whole
				//monster 
				numPieces = quantity; 
			}


			MonsterForUser mfu = createMonsterForUser(userId, monsterId, currentExp,
					currentLvl, currentHealth, numPieces, isComplete, combineStartTime,
					teamSlotNum, sourceOfPieces);
			returnList.add(mfu);
		}
		return returnList;
	}
	
	@Override
	public MonsterForUser createMonsterForUser(UUID userId, int monsterId, int curExp,
			int curLvl, int curHealth, int numPieces, boolean isComplete,
			Date combineStartTime, int teamSlotNum, String sourceOfPieces) {
		
		//not sure if need to save the dude before setting more values
		MonsterForUser mfu = new MonsterForUser();
		mfu.setUserId(userId);
		mfu.setMonsterId(monsterId);
		mfu.setCurrentExp(curExp);
		mfu.setCurrentLvl(curLvl);
		mfu.setCurrentHealth(curHealth);
		mfu.setNumPieces(numPieces);
		mfu.setComplete(isComplete);
		mfu.setCombineStartTime(combineStartTime);
		mfu.setTeamSlotNum(teamSlotNum);
		mfu.setSourceOfPieces(sourceOfPieces);
		
		return mfu;
	}
	
	//returns user monster ids
	@Override
	public List<UUID> selectWholeButNotCombinedUserMonsters(
			Map<UUID, MonsterForUser> idsToUserMonsters) {
		List<UUID> wholeUserMonsterIds = new ArrayList<UUID>();

		Set<Integer> uniqMonsterIds = new HashSet<Integer>();
		for (MonsterForUser mfu : idsToUserMonsters.values()) {
			uniqMonsterIds.add(mfu.getMonsterId());
		}
		//get the monsters in order to determine num pieces to be considered whole
		Map<Integer, Monster> idsToMonsters = getMonsterRetrieveUtils()
				.getMonsterIdsToMonstersForMonsterIds(uniqMonsterIds);

		//loop through user monsters and monsters and see if user monster is whole
		for (UUID userMonsterId : idsToUserMonsters.keySet()) {
			MonsterForUser mfu = idsToUserMonsters.get(userMonsterId);
			int monsterId = mfu.getMonsterId();
			Monster monzter = idsToMonsters.get(monsterId);

			if (mfu.isComplete()) {
				//want only incomplete monsters that are whole
				//(all pieces haven't been combined yet)
				continue;
			}

			int numPiecesToBeWhole = monzter.getNumPuzzlePieces();
			int userMonsterPieces = mfu.getNumPieces();
			if (userMonsterPieces > numPiecesToBeWhole) {
				log.warn("userMonster has more than the max num pieces. userMonster=" +
						mfu + "\t monster=" + monzter);
			} else if (userMonsterPieces == numPiecesToBeWhole) {
				wholeUserMonsterIds.add(userMonsterId);
			}
		}

		return wholeUserMonsterIds;
	}

	@Override
	public float sumProbabilities(List<TaskStageMonster> taskStageMonsters) {
		float sumProbabilities = 0.0f;

		for (TaskStageMonster tsm : taskStageMonsters) {
			sumProbabilities += tsm.getChanceToAppear();
		}

		return sumProbabilities;
	}

	@Override
	public List<MonsterForUser> replaceBattleTeamSlot(int oldTeamSlotNum, int newTeamSlotNum, 
			Map<UUID, MonsterForUser> existingUserMonsters) {
		List<MonsterForUser> modifiedMfuList = new ArrayList<MonsterForUser>();

		for (MonsterForUser mfu : existingUserMonsters.values()) {
			if (mfu.getTeamSlotNum() == oldTeamSlotNum) {
				mfu.setTeamSlotNum(newTeamSlotNum);
				modifiedMfuList.add(mfu);
			}
		}

		return modifiedMfuList;
	}

	
	

	//RETRIEVING STUFF****************************************************************
	
	public List<MonsterForUser> getMonstersForUser(UUID userId) {
		log.debug("retrieving user monsters for userId " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__USER_ID, userId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<MonsterForUser> mfuList = getMonsterForUserEntityManager().get().find(cqlQuery);
		
		return mfuList;
	}
	
	@Override
	public Map<UUID, List<MonsterForUser>> getUserIdsToMonsterTeamForUserIds(
			List<UUID> userIds) {
		log.debug("retrieving user monsters for userIds=" + userIds);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = null;
		String equalityCondDelim = getQueryConstructionUtil().getAnd();
		int lowestTeamSlot = 0;
		Map<String, Object> greaterThanConditions = new HashMap<String, Object>();
		greaterThanConditions.put(MobstersDbTables.MONSTER_FOR_USER__TEAM_SLOT_NUM,
				lowestTeamSlot);
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();
		Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
		inConditions.put(MobstersDbTables.MONSTER_FOR_USER__USER_ID, userIds);
		String inCondDelim = getQueryConstructionUtil().getAnd();
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, inConditions, inCondDelim, delimAcrossConditions, values);
		List<MonsterForUser> mfuList = getMonsterForUserEntityManager().get().find(cqlQuery);
		
		
		Map<UUID, List<MonsterForUser>> retVal = new HashMap<UUID, List<MonsterForUser>>();
		for (MonsterForUser mfu : mfuList) {
			UUID playerId = mfu.getUserId();
			
			if (!retVal.containsKey(playerId)) {
				//since first time seeing this player, create empty list of monsters for him
				List<MonsterForUser> userMonsters = new ArrayList<MonsterForUser>();
				retVal.put(playerId, userMonsters);
			}
			//fill up this player's list of monsters
			List<MonsterForUser> userMonsters = retVal.get(playerId);
			userMonsters.add(mfu);
		}
		
		return retVal;
	}
	
	@Override
	public MonsterForUser getSpecificUserMonster(UUID userMonsterId) {
		log.debug("retrieving user monster for userMonsterId: " + userMonsterId);
		
		MonsterForUser mfu = getMonsterForUserEntityManager().get().get(userMonsterId);
		return mfu;
//		//construct the search parameters
//		Map<String, Object> equalityConditions = new HashMap<String, Object>();
//		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__ID, userMonsterId);
//
//		//query db, "values" is not used 
//		//(its purpose is to hold the values that were supposed to be put
//		// into a prepared statement)
//		List<Object> values = new ArrayList<Object>();
//		boolean preparedStatement = false;
//		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
//				TABLE_NAME, equalityConditions, values, preparedStatement);
//		List<MonsterForUser> mfuList = getMonsterForUserEntityManager().get().find(cqlQuery);
//
//		if (null == mfuList || mfuList.isEmpty()) {
//			log.warn("no MonsterForUser exists for id=" + userMonsterId);
//			return null;
//		} else if (mfuList.size() > 1) {
//			log.warn("multiple MonsterForUser exists for id=" + userMonsterId +
//					"\t monsters=" + mfuList + "\t keeping first one");
//			
//			return mfuList.get(0);
//		} else{
//			MonsterForUser mfu = mfuList.get(0);
//			log.info("retrieved one MonsterForUser. mfu=" + mfu);
//			return mfu;
//		}
	}
	
	@Override
	public Map<UUID, MonsterForUser> getSpecificOrAllUserMonstersForUser(UUID userId,
			Collection<UUID> userMonsterIds) {
		log.debug("retrieving userMonsters for userId=" + userId + "\t userMonsterIds=" +
			userMonsterIds);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__USER_ID, userId);
		String equalityCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Collection<?>> inConditions = null;
		if(null != userMonsterIds && !userMonsterIds.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.MONSTER_FOR_USER__ID, userMonsterIds);
		}
		String inCondDelim = getQueryConstructionUtil().getAnd();
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used
		//(its purpose is to hold the values that were supposed to be put
		//into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(TABLE_NAME,
				equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, inConditions, inCondDelim, delimAcrossConditions, values);
		List<MonsterForUser> mfuList = getMonsterForUserEntityManager().get().find(cqlQuery);
		
		Map<UUID, MonsterForUser> userMonsterIdsToUserMonsters =
				new HashMap<UUID, MonsterForUser>();
		for (MonsterForUser mfu : mfuList) {
			UUID userMonsterId = mfu.getId();
			userMonsterIdsToUserMonsters.put(userMonsterId, mfu);
		}
		
		return userMonsterIdsToUserMonsters;
	}
	
	@Override
	public Map<Integer, MonsterForUser> getIncompleteMonstersWithUserAndMonsterIds(
			UUID userId, Collection<Integer> monsterIds) {
		log.debug("retrieving incomplete userMonsters for userId=" + userId +
				"\t monsterIds=" + monsterIds);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__USER_ID, userId);
		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__IS_COMPLETE, false);
		String equalityCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
		inConditions.put(MobstersDbTables.MONSTER_FOR_USER__MONSTER_ID, monsterIds);
		String inCondDelim = getQueryConstructionUtil().getAnd();
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(TABLE_NAME,
				equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, inConditions, inCondDelim, delimAcrossConditions, values);
		List<MonsterForUser> mfuList = getMonsterForUserEntityManager().get().find(cqlQuery);
		
		Map<Integer, MonsterForUser> monsterIdsToUserMonsters =
				new HashMap<Integer, MonsterForUser>();
		
		//the reason why monsterIdsToUserMonsters is not a HashMap<Integer, List<MonsterForUser>>
		//is because, ideally, for each monster of which the user has at least two, only
		//one should be incomplete
		//e.g. if user has 2+ monsters with id=1 then only one of them should be incomplete
		for (MonsterForUser mfu : mfuList) {
			Integer monsterId = mfu.getMonsterId();
			monsterIdsToUserMonsters.put(monsterId, mfu);
		}
		
		return monsterIdsToUserMonsters;
	}
	
	
	
	//INSERTING STUFF****************************************************************
	
	
	//SAVING STUFF****************************************************************
	@Override
	public void saveUserMonster(MonsterForUser mfu) {
		getMonsterForUserEntityManager().get().put(mfu);
	}
	
	@Override
	public void saveUserMonsters(Collection<MonsterForUser> mfuList) {
		getMonsterForUserEntityManager().get().put(mfuList);
	}
	
	@Override
	public void saveUserMonsters(Collection<MonsterForUser> mfuList, Date combineDate,
			String additionalSop) {
		log.info("(before) saving mfuList=" + mfuList);
		for (MonsterForUser mfu : mfuList) {
			mfu.setCombineStartTime(combineDate);
			
			StringBuilder sb = new StringBuilder();
			sb.append(mfu.getSourceOfPieces());
			sb.append(additionalSop);
			
			String newSop = sb.toString();
			mfu.setSourceOfPieces(newSop);
		}
		
		log.info("(after) saving mfuList=" + mfuList);
		getMonsterForUserEntityManager().get().put(mfuList);
		log.info("saved mfuList");
	}
	
	
	//UPDATING STUFF****************************************************************
	
	//method to reward a user with some monsters
	@Override
	public List<MonsterForUser> updateUserMonstersForUser(UUID userId,
			Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
			Date combineStartDate) {
		log.info("the monster pieces the user gets: " + monsterIdToNumPieces);

		if (monsterIdToNumPieces.isEmpty()) {
			return new ArrayList<MonsterForUser>();
		}

		//for all the monster pieces the user will receive, see if he already has any
		//retrieve all of user's incomplete monsters that have these monster ids 
		Set<Integer> droppedMonsterIds = monsterIdToNumPieces.keySet();

		Map<Integer, MonsterForUser> monsterIdsToIncompletes =
				getIncompleteMonstersWithUserAndMonsterIds(userId, droppedMonsterIds);

		//take however many pieces necessary from monsterIdToNumPieces to
		//complete these incomplete monsterForUsers
		//monsterIdsToIncompletes will be modified
		Map<Integer, Integer> monsterIdToRemainingPieces = 
				completeMonsterForUserFromMonsterIdsAndQuantities(
						monsterIdsToIncompletes, monsterIdToNumPieces);

		//UPDATE THESE INCOMPLETE MONSTERS, IF ANY. SINCE UPDATING, UPDATE THE
		//combineStartTime
		List<MonsterForUser> dirtyMonsterForUserList = 
				new ArrayList<MonsterForUser>(monsterIdsToIncompletes.values());
		if (!dirtyMonsterForUserList.isEmpty()) {
			log.info("the monsters that are updated: " + dirtyMonsterForUserList);
//			saveUserMonsters(dirtyMonsterForUserList, combineStartDate, sourceOfPieces);
		}

		//monsterIdToRemainingPieces now contains all the new monsters
		//the user will get. SET THE combineStartTime
		List<MonsterForUser> newMonsters = createMonstersForUserFromQuantities(
				userId, monsterIdToRemainingPieces, combineStartDate);
		if (!newMonsters.isEmpty()) {
			log.info("the monsters that are new: " + newMonsters);
//			saveUserMonsters(newMonsters, combineStartDate, sourceOfPieces);
		}

		//combine new and updated for one db save call instead of one for each
		List<MonsterForUser> newOrUpdated = new ArrayList<MonsterForUser>();
		newOrUpdated.addAll(dirtyMonsterForUserList);
		newOrUpdated.addAll(newMonsters);
		
		//save these new or updated monsters to the DB
		saveUserMonsters(newOrUpdated, combineStartDate, sourceOfPieces);
		
		return newOrUpdated;
		//	  	List<FullUserMonsterProto> protos = CreateInfoProtoUtils
		//	  			.createFullUserMonsterProtoList(newOrUpdated);
		//	  	return protos;
	}
	
	@Override
	public void updateUserMonstersHealths(Map<UUID, Integer> userMonsterIdToExpectedHealth,
			Map<UUID, MonsterForUser> existingUserMonsters) {
		
		for (UUID mfuId : existingUserMonsters.keySet()) {
			int hp = userMonsterIdToExpectedHealth.get(mfuId);
			MonsterForUser mfu = existingUserMonsters.get(mfuId);
			
			mfu.setCurrentHealth(hp);
		}
		saveUserMonsters(existingUserMonsters.values());
	}
	
	@Override
	public void updateBattleTeamSlot(int newTeamSlotNum, MonsterForUser mfu) {
		mfu.setTeamSlotNum(newTeamSlotNum);
		saveUserMonster(mfu);
	}
	
	
	// DELETING STUFF****************************************************************
	
	public void deleteUserMonster(UUID deleteUserMonsterUuid) {
		getMonsterForUserEntityManager().get().delete(deleteUserMonsterUuid);
	}
	
	public void deleteUserMonsters(List<UUID> deleteUserMonstersList) {
		getMonsterForUserEntityManager().get().delete(deleteUserMonstersList);
	}
	
	
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public MonsterForUserEntityManager getMonsterForUserEntityManager() {
		return monsterForUserEntityManager;
	}
	@Override
	public void setMonsterForUserEntityManager(
			MonsterForUserEntityManager monsterForUserEntityManager) {
		this.monsterForUserEntityManager = monsterForUserEntityManager;
	}
	@Override
	public MonsterRetrieveUtils getMonsterRetrieveUtils() {
		return monsterRetrieveUtils;
	}
	@Override
	public void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils) {
		this.monsterRetrieveUtils = monsterRetrieveUtils;
	}
	@Override
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	@Override
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	
	
	
	
	
	/*
	//OLD AOC2 STUFF****************************************************************
	@Override
	public Map<UUID, MonsterForUser> getUserEquipsByUserEquipIds(Collection<UUID> ids) {
		Map<UUID, MonsterForUser> returnVal = new HashMap<UUID, MonsterForUser>();
		
		List<MonsterForUser> ueList = monsterForUserEntityManager.get().get(ids);
		for (MonsterForUser ue : ueList) {
			UUID id = ue.getId();
			returnVal.put(id, ue);
		}
		
		return returnVal;
	}
	
	@Override
	public void saveEquips(Collection<MonsterForUser> newEquips) {
		getMonsterForUserEntityManager().get().put(newEquips);
	}
	
	@Override
	public void getEquippedUserEquips(List<MonsterForUser> allUserEquips, List<MonsterForUser> equippedUserEquips) {
		for(MonsterForUser ue : allUserEquips) {
			if(ue.isEquipped())
				equippedUserEquips.add(ue);
		}
	}
	
	public  MonsterForUser getUserEquipForId(UUID id) {
		log.debug("retrieve MonsterForUser data for id " + id);
		if (idsToUserEquips == null) {
			setStaticIdsToUserEquips();      
		}
		return idsToUserEquips.get(id);
	}

	public  Map<UUID, MonsterForUser> getUserEquipsForIds(List<UUID> ids) {
		log.debug("retrieve UserEquips data for ids " + ids);
		if (idsToUserEquips == null) {
			setStaticIdsToUserEquips();      
		}
		Map<UUID, MonsterForUser> toreturn = new HashMap<UUID, MonsterForUser>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToUserEquips.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToUserEquips() {
		log.debug("setting  map of UserEquipIds to UserEquips");

		String cqlquery = "select * from user_equip;"; 
		List <MonsterForUser> list = getMonsterForUserEntityManager().get().find(cqlquery);
		idsToUserEquips = new HashMap<UUID, MonsterForUser>();
		for(MonsterForUser us : list) {
			UUID id= us.getId();
			idsToUserEquips.put(id, us);
		}
					
	}

	public  List<MonsterForUser> getAllUserEquipsForUser(UUID userId) {
		String cqlquery = "select * from user_equip where user_id=" + userId + ";"; 
		List <MonsterForUser> list = getMonsterForUserEntityManager().get().find(cqlquery);
		return list;
	}
	
	public StructureLab getEquipmentCorrespondingToUserEquip(MonsterForUser ue) {
		UUID equipId = ue.getEquipId();
		return getEquipmentRetrieveUtils().getEquipmentForId(equipId);
	}
	
	public List<MonsterForUser> getAllEquippedUserEquipsForUser(UUID userId) {
		List<MonsterForUser> ueList = getAllUserEquipsForUser(userId);
		List<MonsterForUser> equippedList = new ArrayList<>();
		for(MonsterForUser ue : ueList) {
			if(ue.isEquipped()) {
				equippedList.add(ue);
			}
		}
		return equippedList;
	}
	*/
	
		
}