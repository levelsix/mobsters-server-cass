package com.lvl6.mobsters.services.monsterhealingforuser;

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

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class MonsterHealingForUserServiceImpl implements MonsterHealingForUserService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER_HEALING_FOR_USER;
	
	@Autowired
	protected MonsterHealingForUserEntityManager monsterHealingForUserEntityManager;
	
	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	

	//RETRIEVING STUFF****************************************************************
	
	public Map<UUID, MonsterHealingForUser> getMonstersHealingForUser(UUID userId) {
		log.debug("retrieving user monsters for userId " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_HEALING_FOR_USER__USER_ID, userId);
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<MonsterHealingForUser> mefuList = getMonsterHealingForUserEntityManager().get().find(cqlQuery);
		
		
		Map<UUID, MonsterHealingForUser> userMonsterIdsToUserMonstersHealing =
				new HashMap<UUID, MonsterHealingForUser>();
		for (MonsterHealingForUser mefu : mefuList) {
			UUID userMonsterId = mefu.getId();
			userMonsterIdsToUserMonstersHealing.put(userMonsterId, mefu);
		}
		
		return userMonsterIdsToUserMonstersHealing;
	}
	
//	@Override
//	public MonsterHealingForUser getSpecificUserMonster(UUID userMonsterId) {
//		log.debug("retrieving user monster for userMonsterId: " + userMonsterId);
//		
//		MonsterHealingForUser mfu = getMonsterHealingForUserEntityManager().get().get(userMonsterId);
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
//		List<MonsterHealingForUser> mfuList = getMonsterHealingForUserEntityManager().get().find(cqlQuery);
//
//		if (null == mfuList || mfuList.isEmpty()) {
//			log.warn("no MonsterHealingForUser exists for id=" + userMonsterId);
//			return null;
//		} else if (mfuList.size() > 1) {
//			log.warn("multiple MonsterHealingForUser exists for id=" + userMonsterId +
//					"\t monsters=" + mfuList + "\t keeping first one");
//			
//			return mfuList.get(0);
//		} else{
//			MonsterHealingForUser mfu = mfuList.get(0);
//			log.info("retrieved one MonsterHealingForUser. mfu=" + mfu);
//			return mfu;
//		}
//	}
	
	@Override
	public Map<UUID, MonsterHealingForUser> getSpecificOrAllUserMonstersHealingForUser(UUID userId,
			Collection<UUID> userMonsterIds) {
		log.debug("retrieving userMonsters for userId=" + userId + "\t userMonsterIds=" +
			userMonsterIds);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_HEALING_FOR_USER__USER_ID, userId);
		String equalityCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Object> greaterThanConditions = null;
		String greaterThanCondDelim = getQueryConstructionUtil().getAnd();
		
		Map<String, Collection<?>> inConditions = null;
		if(null != userMonsterIds && !userMonsterIds.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.MONSTER_HEALING_FOR_USER__ID, userMonsterIds);
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
		List<MonsterHealingForUser> mfuList = getMonsterHealingForUserEntityManager().get().find(cqlQuery);
		
		Map<UUID, MonsterHealingForUser> userMonsterIdsToUserMonsters =
				new HashMap<UUID, MonsterHealingForUser>();
		for (MonsterHealingForUser mfu : mfuList) {
			UUID userMonsterId = mfu.getId();
			userMonsterIdsToUserMonsters.put(userMonsterId, mfu);
		}
		
		return userMonsterIdsToUserMonsters;
	}
	
	
	//INSERTING STUFF****************************************************************
	
	
	//SAVING STUFF****************************************************************
	
	@Override
	public void saveUserMonsters(List<MonsterHealingForUser> mfuList, Date combineDate,
			String additionalSop) {
//		log.info("(before) saving mfuList=" + mfuList);
//		for (MonsterHealingForUser mfu : mfuList) {
//			mfu.setCombineStartTime(combineDate);
//			
//			StringBuilder sb = new StringBuilder();
//			sb.append(mfu.getSourceOfPieces());
//			sb.append(additionalSop);
//			
//			String newSop = sb.toString();
//			mfu.setSourceOfPieces(newSop);
//		}
//		
//		log.info("(after) saving mfuList=" + mfuList);
//		getMonsterHealingForUserEntityManager().get().put(mfuList);
//		log.info("saved mfuList");
	}
	
	
	//UPDATING STUFF****************************************************************
	
	//method to reward a user with some monsters
//	@Override
//	public List<MonsterHealingForUser> updateUserMonstersHealingForUser(UUID userId,
//			Map<Integer, Integer> monsterIdToNumPieces, String sourceOfPieces,
//			Date combineStartDate) {
//		log.info("the monster pieces the user gets: " + monsterIdToNumPieces);
//
//		if (monsterIdToNumPieces.isEmpty()) {
//			return new ArrayList<MonsterHealingForUser>();
//		}
//
//		//for all the monster pieces the user will receive, see if he already has any
//		//retrieve all of user's incomplete monsters that have these monster ids 
//		Set<Integer> droppedMonsterIds = monsterIdToNumPieces.keySet();
//
//		Map<Integer, MonsterHealingForUser> monsterIdsToIncompletes =
//				getIncompleteMonstersWithUserAndMonsterIds(userId, droppedMonsterIds);
//
//		//take however many pieces necessary from monsterIdToNumPieces to
//		//complete these incomplete monsterHealingForUsers
//		//monsterIdsToIncompletes will be modified
//		Map<Integer, Integer> monsterIdToRemainingPieces = 
//				completeMonsterHealingForUserFromMonsterIdsAndQuantities(
//						monsterIdsToIncompletes, monsterIdToNumPieces);
//
//		//UPDATE THESE INCOMPLETE MONSTERS, IF ANY. SINCE UPDATING, UPDATE THE
//		//combineStartTime
//		List<MonsterHealingForUser> dirtyMonsterHealingForUserList = 
//				new ArrayList<MonsterHealingForUser>(monsterIdsToIncompletes.values());
//		if (!dirtyMonsterHealingForUserList.isEmpty()) {
//			log.info("the monsters that are updated: " + dirtyMonsterHealingForUserList);
////			saveUserMonsters(dirtyMonsterHealingForUserList, combineStartDate, sourceOfPieces);
//		}
//
//		//monsterIdToRemainingPieces now contains all the new monsters
//		//the user will get. SET THE combineStartTime
//		List<MonsterHealingForUser> newMonsters = createMonstersHealingForUserFromQuantities(
//				userId, monsterIdToRemainingPieces, combineStartDate);
//		if (!newMonsters.isEmpty()) {
//			log.info("the monsters that are new: " + newMonsters);
////			saveUserMonsters(newMonsters, combineStartDate, sourceOfPieces);
//		}
//
//		//combine new and updated for one db save call instead of one for each
//		List<MonsterHealingForUser> newOrUpdated = new ArrayList<MonsterHealingForUser>();
//		newOrUpdated.addAll(dirtyMonsterHealingForUserList);
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
	
	public void deleteUserMonster(UUID deleteUserMonsterUuid) {
		getMonsterHealingForUserEntityManager().get().delete(deleteUserMonsterUuid);
	}
	
	public void deleteUserMonsters(List<UUID> deleteUserMonstersList) {
		getMonsterHealingForUserEntityManager().get().delete(deleteUserMonstersList);
	}
	
	
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public MonsterHealingForUserEntityManager getMonsterHealingForUserEntityManager() {
		return monsterHealingForUserEntityManager;
	}
	@Override
	public void setMonsterHealingForUserEntityManager(
			MonsterHealingForUserEntityManager monsterHealingForUserEntityManager) {
		this.monsterHealingForUserEntityManager = monsterHealingForUserEntityManager;
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
