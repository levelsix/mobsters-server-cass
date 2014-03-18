package com.lvl6.mobsters.services.monsterhealingforuser;

import java.util.ArrayList;
import java.util.Collection;
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
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
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
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	//CONTROLLER LOGIC STUFF****************************************************************
	

	//RETRIEVING STUFF****************************************************************
	@Override
	public Map<UUID, MonsterHealingForUser> getUserMonsterIdsToUserMonstersHealingForUser(UUID userId) {
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
		boolean allowFiltering = true; //need cassandra to query with non row keys
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values,
				preparedStatement, allowFiltering);
		List<MonsterHealingForUser> mefuList = getMonsterHealingForUserEntityManager().get().find(cqlQuery);
		
		
		Map<UUID, MonsterHealingForUser> userMonsterIdsToUserMonstersHealing =
				new HashMap<UUID, MonsterHealingForUser>();
		for (MonsterHealingForUser mefu : mefuList) {
			UUID userMonsterId = mefu.getMonsterForUserId();
			userMonsterIdsToUserMonstersHealing.put(userMonsterId, mefu);
		}
		
		return userMonsterIdsToUserMonstersHealing;
	}
	
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
		List<MonsterHealingForUser> mfuList = getMonsterHealingForUserEntityManager().get().find(cqlQuery);
		
		Map<UUID, MonsterHealingForUser> userMonsterIdsToUserMonsters =
				new HashMap<UUID, MonsterHealingForUser>();
		for (MonsterHealingForUser mfu : mfuList) {
			UUID userMonsterId = mfu.getMonsterForUserId();
			userMonsterIdsToUserMonsters.put(userMonsterId, mfu);
		}
		
		return userMonsterIdsToUserMonsters;
	}
	
	
	//INSERTING STUFF****************************************************************
	
	
	//SAVING STUFF****************************************************************
	@Override
	public void saveUserMonsterHealing(Collection<MonsterHealingForUser> mhfuList) {
		getMonsterHealingForUserEntityManager().get().put(mhfuList);
	}
	
	
	//UPDATING STUFF****************************************************************
	@Override
	public void healUserMonsters(Map<UUID, Integer> userMonsterIdsToHealths,
			Map<UUID, MonsterForUser> idsToUserMonsters) {
		Collection<MonsterForUser> healedMonsters = new ArrayList<MonsterForUser>();
		Collection<UUID> mfuIds = userMonsterIdsToHealths.keySet();
		
		for (UUID mfuId : mfuIds) {
			int newHp = userMonsterIdsToHealths.get(mfuId);
			
			if (idsToUserMonsters.containsKey(mfuId)) {
				MonsterForUser mfu = idsToUserMonsters.get(mfuId);
				mfu.setCurrentHealth(newHp);
				healedMonsters.add(mfu);
			}
		}
		
		if (!healedMonsters.isEmpty()) {
			getMonsterForUserService().saveUserMonsters(healedMonsters);
			deleteUserMonstersHealing(mfuIds);
		}
	}
	
	// DELETING STUFF****************************************************************
	@Override
	public void deleteUserMonsterHealing(UUID deleteUserMonsterUuid) {
		getMonsterHealingForUserEntityManager().get().delete(deleteUserMonsterUuid);
	}
	@Override
	public void deleteUserMonstersHealing(Collection<UUID> deleteUserMonstersList) {
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
	@Override
	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}
	@Override
	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

}
