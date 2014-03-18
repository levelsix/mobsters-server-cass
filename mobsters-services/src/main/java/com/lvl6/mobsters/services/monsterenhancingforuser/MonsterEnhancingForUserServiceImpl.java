package com.lvl6.mobsters.services.monsterenhancingforuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterEnhancingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class MonsterEnhancingForUserServiceImpl implements MonsterEnhancingForUserService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER_ENHANCING_FOR_USER;
	
	@Autowired
	protected MonsterEnhancingForUserEntityManager monsterEnhancingForUserEntityManager;
	
	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	@Override
	public UUID selectBaseEnhancingMonsterId(Map<UUID, MonsterEnhancingForUser> mefuIdToMefu) {
		for (UUID mefuId : mefuIdToMefu.keySet()) {
			MonsterEnhancingForUser mefu = mefuIdToMefu.get(mefuId);
			
			Date startDate = mefu.getExpectedStartTime();
			if (null == startDate) {
				return mefuId;
			}
		}
		return null;
	}

	//RETRIEVING STUFF****************************************************************
	@Override
	public Map<UUID, MonsterEnhancingForUser> getMonstersEnhancingForUser(UUID userId) {
		log.debug("retrieving user monsters for userId " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_ENHANCING_FOR_USER__USER_ID, userId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		boolean allowFiltering = true; //need cassandra to query with non row keys
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values,
				preparedStatement, allowFiltering);
		List<MonsterEnhancingForUser> mefuList = getMonsterEnhancingForUserEntityManager().get().find(cqlQuery);
		
		
		Map<UUID, MonsterEnhancingForUser> userMonsterIdsToUserMonstersEnhancing =
				new HashMap<UUID, MonsterEnhancingForUser>();
		for (MonsterEnhancingForUser mefu : mefuList) {
			UUID userMonsterId = mefu.getId();
			userMonsterIdsToUserMonstersEnhancing.put(userMonsterId, mefu);
		}
		
		return userMonsterIdsToUserMonstersEnhancing;
	}
	
//	@Override
//	public MonsterEnhancingForUser getSpecificUserMonster(UUID userMonsterId) {
//		log.debug("retrieving user monster for userMonsterId: " + userMonsterId);
//		
//		MonsterEnhancingForUser mfu = getMonsterEnhancingForUserEntityManager().get().get(userMonsterId);
//		return mfu;
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
//		List<MonsterEnhancingForUser> mfuList = getMonsterEnhancingForUserEntityManager().get().find(cqlQuery);
//
//		if (null == mfuList || mfuList.isEmpty()) {
//			log.warn("no MonsterEnhancingForUser exists for id=" + userMonsterId);
//			return null;
//		} else if (mfuList.size() > 1) {
//			log.warn("multiple MonsterEnhancingForUser exists for id=" + userMonsterId +
//					"\t monsters=" + mfuList + "\t keeping first one");
//			
//			return mfuList.get(0);
//		} else{
//			MonsterEnhancingForUser mfu = mfuList.get(0);
//			log.info("retrieved one MonsterEnhancingForUser. mfu=" + mfu);
//			return mfu;
//		}
//	}
	
	@Override
	public Map<UUID, MonsterEnhancingForUser> getSpecificOrAllUserMonstersEnhancingForUser(UUID userId,
			Collection<UUID> userMonsterIds) {
		log.debug("retrieving userMonsters for userId=" + userId + "\t userMonsterIds=" +
			userMonsterIds);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_ENHANCING_FOR_USER__USER_ID, userId);
		String equalityCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Collection<?>> inConditions = null;
		if(null != userMonsterIds && !userMonsterIds.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.MONSTER_ENHANCING_FOR_USER__ID, userMonsterIds);
		}
		String inCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Collection<?>> isConditions = null;
		String isCondDelim = null;
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used
		//(its purpose is to hold the values that were supposed to be put
		//into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean allowFiltering = true; //need cassandra to query with non row keys
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, inConditions, inCondDelim, isConditions, isCondDelim,
				delimAcrossConditions, values, allowFiltering);
		List<MonsterEnhancingForUser> mfuList = getMonsterEnhancingForUserEntityManager().get().find(cqlQuery);
		
		Map<UUID, MonsterEnhancingForUser> userMonsterIdsToUserMonsters =
				new HashMap<UUID, MonsterEnhancingForUser>();
		for (MonsterEnhancingForUser mfu : mfuList) {
			UUID userMonsterId = mfu.getId();
			userMonsterIdsToUserMonsters.put(userMonsterId, mfu);
		}
		
		return userMonsterIdsToUserMonsters;
	}
	
	
	//INSERTING STUFF****************************************************************
	
	
	//SAVING STUFF****************************************************************
	
	@Override
	public void saveUserMonsters(List<MonsterEnhancingForUser> mefuList) {
		getMonsterEnhancingForUserEntityManager().get().put(mefuList);
	}
	
	
	//UPDATING STUFF****************************************************************
	
	//method to reward a user with some monsters
//	@Override
//	public List<MonsterEnhancingForUser> updateUserMonstersEnhancingForUser(UUID userId,
//			Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
//			Date combineStartDate) {
//		log.info("the monster pieces the user gets: " + monsterIdToNumPieces);
//
//		if (monsterIdToNumPieces.isEmpty()) {
//			return new ArrayList<MonsterEnhancingForUser>();
//		}
//
//		//for all the monster pieces the user will receive, see if he already has any
//		//retrieve all of user's incomplete monsters that have these monster ids 
//		Set<Integer> droppedMonsterIds = monsterIdToNumPieces.keySet();
//
//		Map<Integer, MonsterEnhancingForUser> monsterIdsToIncompletes =
//				getIncompleteMonstersWithUserAndMonsterIds(userId, droppedMonsterIds);
//
//		//take however many pieces necessary from monsterIdToNumPieces to
//		//complete these incomplete monsterEnhancingForUsers
//		//monsterIdsToIncompletes will be modified
//		Map<Integer, Integer> monsterIdToRemainingPieces = 
//				completeMonsterEnhancingForUserFromMonsterIdsAndQuantities(
//						monsterIdsToIncompletes, monsterIdToNumPieces);
//
//		//UPDATE THESE INCOMPLETE MONSTERS, IF ANY. SINCE UPDATING, UPDATE THE
//		//combineStartTime
//		List<MonsterEnhancingForUser> dirtyMonsterEnhancingForUserList = 
//				new ArrayList<MonsterEnhancingForUser>(monsterIdsToIncompletes.values());
//		if (!dirtyMonsterEnhancingForUserList.isEmpty()) {
//			log.info("the monsters that are updated: " + dirtyMonsterEnhancingForUserList);
////			saveUserMonsters(dirtyMonsterEnhancingForUserList, combineStartDate, sourceOfPieces);
//		}
//
//		//monsterIdToRemainingPieces now contains all the new monsters
//		//the user will get. SET THE combineStartTime
//		List<MonsterEnhancingForUser> newMonsters = createMonstersEnhancingForUserFromQuantities(
//				userId, monsterIdToRemainingPieces, combineStartDate);
//		if (!newMonsters.isEmpty()) {
//			log.info("the monsters that are new: " + newMonsters);
////			saveUserMonsters(newMonsters, combineStartDate, sourceOfPieces);
//		}
//
//		//combine new and updated for one db save call instead of one for each
//		List<MonsterEnhancingForUser> newOrUpdated = new ArrayList<MonsterEnhancingForUser>();
//		newOrUpdated.addAll(dirtyMonsterEnhancingForUserList);
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
	
	public void deleteUserMonsterEnhancing(UUID userMonsterEnhancingUuid) {
		getMonsterEnhancingForUserEntityManager().get().delete(userMonsterEnhancingUuid);
	}
	
	public void deleteUserMonstersEnhancing(List<UUID> userMonstersEnhancingList) {
		getMonsterEnhancingForUserEntityManager().get().delete(userMonstersEnhancingList);
	}
	
	
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public MonsterEnhancingForUserEntityManager getMonsterEnhancingForUserEntityManager() {
		return monsterEnhancingForUserEntityManager;
	}
	@Override
	public void setMonsterEnhancingForUserEntityManager(
			MonsterEnhancingForUserEntityManager monsterEnhancingForUserEntityManager) {
		this.monsterEnhancingForUserEntityManager = monsterEnhancingForUserEntityManager;
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
