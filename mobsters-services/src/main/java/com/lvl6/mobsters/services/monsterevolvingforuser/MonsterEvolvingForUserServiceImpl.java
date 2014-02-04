package com.lvl6.mobsters.services.monsterevolvingforuser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterEvolvingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEvolvingForUser;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class MonsterEvolvingForUserServiceImpl implements MonsterEvolvingForUserService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER_EVOLVING_FOR_USER;
	
	@Autowired
	protected MonsterEvolvingForUserEntityManager monsterEvolvingForUserEntityManager;
	
	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	private void orderMonsterEvolvingForUserAscending(List<MonsterEvolvingForUser> mefuList) {
		Collections.sort(mefuList, new Comparator<MonsterEvolvingForUser>() {
			@Override
			public int compare(MonsterEvolvingForUser lhs, MonsterEvolvingForUser rhs) {
				//sorting by accept time, which should not be null
				Date lhsDate = lhs.getStartTime();
				Date rhsDate = rhs.getStartTime();

				if (null == lhsDate && null == rhsDate) 
					return 0;
				else if (null == lhsDate) 
					return -1;
				else if (null == rhsDate) 
					return 1;
				else if (lhsDate.getTime() < rhsDate.getTime())
					return -1;
				else if (lhsDate.getTime() == rhsDate.getTime())
					return 0;
				else
					return 1;
			}
			
		});
	}
	
	@Override
	public Set<UUID> getMonsterForUserIdsFromEvolution(MonsterEvolvingForUser mefu) {
		Set<UUID> mfuIds = new HashSet<UUID>();
		
		mfuIds.add(mefu.getCatalystMonsterForUserId());
		mfuIds.add(mefu.getMonsterForUserIdOne());
		mfuIds.add(mefu.getMonsterForUserIdTwo());
		
		return mfuIds;
	}

	//RETRIEVING STUFF****************************************************************
	
	@Override
	public Map<UUID, MonsterEvolvingForUser> getCatalystIdsToEvolutionsForUser(UUID userId) {
		log.debug("retrieving user monsters evolving for userId " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_EVOLVING_FOR_USER__USER_ID, userId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<MonsterEvolvingForUser> mefuList = getMonsterEvolvingForUserEntityManager().get().find(cqlQuery);
		
		
		Map<UUID, MonsterEvolvingForUser> userMonsterIdsToUserMonstersEvolving =
				new HashMap<UUID, MonsterEvolvingForUser>();
		for (MonsterEvolvingForUser mefu : mefuList) {
			UUID catalystId = mefu.getCatalystMonsterForUserId();
			userMonsterIdsToUserMonstersEvolving.put(catalystId, mefu);
		}
		
		return userMonsterIdsToUserMonstersEvolving;
	}
	
	//AT THE MOMENT, A USER CAN ONLY HAVE AT MOST ONE EVOLUTION GOING AT ANY TIME
	@Override
	public MonsterEvolvingForUser getEvolutionForUser(UUID userId) {
		log.debug("retrieving user monster evolving for userId " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_EVOLVING_FOR_USER__USER_ID, userId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<MonsterEvolvingForUser> mefuList = getMonsterEvolvingForUserEntityManager().get().find(cqlQuery);

		
		if (null == mefuList) {
			//no evolution exists for user
			return null;
		}
		
		if (mefuList.size() > 1) {
			log.error("user has more than one evolution. Keeping most recent. evolutions= " +
					mefuList);
			orderMonsterEvolvingForUserAscending(mefuList);
			int lastIndex = mefuList.size() - 1;
			return mefuList.get(lastIndex);
		}
		
		//user only has one evolution
		return mefuList.get(0);
	}
	
	
	//INSERTING STUFF****************************************************************
	public void insertIntoMonsterEvolvingForUser(UUID userId, Date startTime,
			UUID catalystUserMonsterId, List<UUID> userMonsterIds) {
		if (null == userMonsterIds || userMonsterIds.size() < 2) {
			return;
		}
		MonsterEvolvingForUser mefu = new MonsterEvolvingForUser();
		mefu.setUserId(userId);
		mefu.setCatalystMonsterForUserId(catalystUserMonsterId);
		mefu.setMonsterForUserIdOne(userMonsterIds.get(0));
		mefu.setMonsterForUserIdTwo(userMonsterIds.get(1));
		mefu.setStartTime(startTime);
	}
	
	
	//SAVING STUFF****************************************************************
	
	@Override
	public void saveUserMonsters(List<MonsterEvolvingForUser> mefuList) {
		getMonsterEvolvingForUserEntityManager().get().put(mefuList);
	}
	
	
	//UPDATING STUFF****************************************************************
	
	//method to reward a user with some monsters
//	@Override
//	public List<MonsterEvolvingForUser> updateUserMonstersEvolvingForUser(UUID userId,
//			Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
//			Date combineStartDate) {
//		log.info("the monster pieces the user gets: " + monsterIdToNumPieces);
//
//		if (monsterIdToNumPieces.isEmpty()) {
//			return new ArrayList<MonsterEvolvingForUser>();
//		}
//
//		//for all the monster pieces the user will receive, see if he already has any
//		//retrieve all of user's incomplete monsters that have these monster ids 
//		Set<Integer> droppedMonsterIds = monsterIdToNumPieces.keySet();
//
//		Map<Integer, MonsterEvolvingForUser> monsterIdsToIncompletes =
//				getIncompleteMonstersWithUserAndMonsterIds(userId, droppedMonsterIds);
//
//		//take however many pieces necessary from monsterIdToNumPieces to
//		//complete these incomplete monsterEvolvingForUsers
//		//monsterIdsToIncompletes will be modified
//		Map<Integer, Integer> monsterIdToRemainingPieces = 
//				completeMonsterEvolvingForUserFromMonsterIdsAndQuantities(
//						monsterIdsToIncompletes, monsterIdToNumPieces);
//
//		//UPDATE THESE INCOMPLETE MONSTERS, IF ANY. SINCE UPDATING, UPDATE THE
//		//combineStartTime
//		List<MonsterEvolvingForUser> dirtyMonsterEvolvingForUserList = 
//				new ArrayList<MonsterEvolvingForUser>(monsterIdsToIncompletes.values());
//		if (!dirtyMonsterEvolvingForUserList.isEmpty()) {
//			log.info("the monsters that are updated: " + dirtyMonsterEvolvingForUserList);
////			saveUserMonsters(dirtyMonsterEvolvingForUserList, combineStartDate, sourceOfPieces);
//		}
//
//		//monsterIdToRemainingPieces now contains all the new monsters
//		//the user will get. SET THE combineStartTime
//		List<MonsterEvolvingForUser> newMonsters = createMonstersEvolvingForUserFromQuantities(
//				userId, monsterIdToRemainingPieces, combineStartDate);
//		if (!newMonsters.isEmpty()) {
//			log.info("the monsters that are new: " + newMonsters);
////			saveUserMonsters(newMonsters, combineStartDate, sourceOfPieces);
//		}
//
//		//combine new and updated for one db save call instead of one for each
//		List<MonsterEvolvingForUser> newOrUpdated = new ArrayList<MonsterEvolvingForUser>();
//		newOrUpdated.addAll(dirtyMonsterEvolvingForUserList);
//		newOrUpdated.addAll(newMonsters);
//		
//		//save these new or updated monsters to the DB
//		saveUserMonsters(newOrUpdated, combineStartDate, sourceOfPieces);
//		
//		return newOrUpdated;
//		//	  	List<FullUserMonsterProto> protos = CreateInfoProtoUtils
//		//	  			.createFullUserMonsterProtoList(newOrUpdated);
//		//	  	return protos;
//	}
	
	// DELETING STUFF****************************************************************
	
	public void deleteUserMonsterEvolving(UUID userMonsterEvolvingUuid) {
		getMonsterEvolvingForUserEntityManager().get().delete(userMonsterEvolvingUuid);
	}
	
	public void deleteUserMonstersEvolving(List<UUID> userMonstersEvolvingList) {
		getMonsterEvolvingForUserEntityManager().get().delete(userMonstersEvolvingList);
	}
	
	
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public MonsterEvolvingForUserEntityManager getMonsterEvolvingForUserEntityManager() {
		return monsterEvolvingForUserEntityManager;
	}
	@Override
	public void setMonsterEvolvingForUserEntityManager(
			MonsterEvolvingForUserEntityManager monsterEvolvingForUserEntityManager) {
		this.monsterEvolvingForUserEntityManager = monsterEvolvingForUserEntityManager;
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

}
