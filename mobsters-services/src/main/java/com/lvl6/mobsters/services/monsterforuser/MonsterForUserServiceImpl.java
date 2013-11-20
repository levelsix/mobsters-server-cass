package com.lvl6.mobsters.services.monsterforuser;

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

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
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
	private QueryConstructionUtil queryConstructionUtil;
	
	

	//RETRIEVING STUFF
	public List<MonsterForUser> getMonstersForUser(UUID userId) {
		log.debug("retrieving user monsters for userId " + userId);

		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__USER_ID, userId);
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, values, preparedStatement);
		List<MonsterForUser> mfuList = getMonsterForUserEntityManager().get().find(cqlQuery);
		
		return mfuList;
	}
	
	@Override
	public Map<UUID, List<MonsterForUser>> getUserIdsToMonsterTeamForUserIds(
			List<UUID> userIds) {
		log.debug("retrieving user monsters for userIds=" + userIds);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = null;
		int lowestTeamSlot = 0;
		Map<String, Object> greaterThanConditions = new HashMap<String, Object>();
		greaterThanConditions.put(MobstersDbTables.MONSTER_FOR_USER__TEAM_SLOT_NUM,
				lowestTeamSlot);
		Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
		inConditions.put(MobstersDbTables.MONSTER_FOR_USER__USER_ID, userIds);
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, greaterThanConditions,
				inConditions, values);
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
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__ID, userMonsterId);

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, values, preparedStatement);
		List<MonsterForUser> mfuList = getMonsterForUserEntityManager().get().find(cqlQuery);

		if (null == mfuList || mfuList.isEmpty()) {
			log.warn("no MonsterForUser exists for id=" + userMonsterId);
			return null;
		} else if (mfuList.size() > 1) {
			log.warn("multiple MonsterForUser exists for id=" + userMonsterId +
					"\t monsters=" + mfuList + "\t keeping first one");
			
			return mfuList.get(0);
		} else{
			MonsterForUser mfu = mfuList.get(0);
			log.info("retrieved one MonsterForUser. mfu=" + mfu);
			return mfu;
		}
	}
	
	@Override
	public Map<UUID, MonsterForUser> getSpecificOrAllUserMonstersForUser(UUID userId,
			Collection<UUID> userMonsterIds) {
		log.debug("retrieving userMonsters for userId=" + userId + "\t userMonsterIds=" +
			userMonsterIds);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__USER_ID, userId);
		Map<String, Object> greaterThanConditions = null;
		Map<String, Collection<?>> inConditions = null;
		if(null != userMonsterIds && !userMonsterIds.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.MONSTER_FOR_USER__ID, userMonsterIds);
		}
		
		//query db, "values" is not used
		//(its purpose is to hold the values that were supposed to be put
		//into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(TABLE_NAME,
				equalityConditions, greaterThanConditions, inConditions, values);
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
			int userId, Collection<Integer> monsterIds) {
		log.debug("retrieving incomplete userMonsters for userId=" + userId +
				"\t monsterIds=" + monsterIds);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__USER_ID, userId);
		equalityConditions.put(MobstersDbTables.MONSTER_FOR_USER__IS_COMPLETE, false);
		Map<String, Object> greaterThanConditions = null;
		Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
		inConditions.put(MobstersDbTables.MONSTER_FOR_USER__MONSTER_ID, monsterIds);
		
		//query db, "values" is not used
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(TABLE_NAME,
				equalityConditions, greaterThanConditions, inConditions, values);
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
	
	
	
	//INSERTING STUFF
	
	
	
	
	
	//for the setter dependency injection or something
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
	//OLD AOC2 STUFF
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
	
	public Equipment getEquipmentCorrespondingToUserEquip(MonsterForUser ue) {
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