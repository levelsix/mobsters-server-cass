package com.lvl6.mobsters.services.monsterforuserdeleted;

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

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserDeletedEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.MonsterRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUserDeleted;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class MonsterForUserDeletedServiceImpl implements MonsterForUserDeletedService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private final String TABLE_NAME = MobstersDbTables.TABLE_MONSTER_FOR_USER_DELETED;
	
	@Autowired
	protected MonsterForUserDeletedEntityManager monsterForUserDeletedEntityManager;
	
	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	//CONTROLLER LOGIC STUFF****************************************************************
	
	
	

	//RETRIEVING STUFF****************************************************************
	
	public List<MonsterForUserDeleted> getMonstersForUser(UUID userId) {
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
		List<MonsterForUserDeleted> mfuList = getMonsterForUserDeletedEntityManager().get().find(cqlQuery);
		
		return mfuList;
	}
	
	@Override
	public Map<UUID, List<MonsterForUserDeleted>> getUserIdsToMonsterTeamForUserIds(
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

		Map<String, Collection<?>> isConditions = null;
		String isCondDelim = null;
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, isConditions, isCondDelim, inConditions,
				inCondDelim, delimAcrossConditions, values);
		List<MonsterForUserDeleted> mfuList = getMonsterForUserDeletedEntityManager().get().find(cqlQuery);
		
		
		Map<UUID, List<MonsterForUserDeleted>> retVal = new HashMap<UUID, List<MonsterForUserDeleted>>();
		for (MonsterForUserDeleted mfu : mfuList) {
			UUID playerId = mfu.getUserId();
			
			if (!retVal.containsKey(playerId)) {
				//since first time seeing this player, create empty list of monsters for him
				List<MonsterForUserDeleted> userMonsters = new ArrayList<MonsterForUserDeleted>();
				retVal.put(playerId, userMonsters);
			}
			//fill up this player's list of monsters
			List<MonsterForUserDeleted> userMonsters = retVal.get(playerId);
			userMonsters.add(mfu);
		}
		
		return retVal;
	}
	
	@Override
	public MonsterForUserDeleted getSpecificUserMonster(UUID userMonsterId) {
		log.debug("retrieving user monster for userMonsterId: " + userMonsterId);
		
		MonsterForUserDeleted mfu = getMonsterForUserDeletedEntityManager().get().get(userMonsterId);
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
//		List<MonsterForUserDeleted> mfuList = getMonsterForUserDeletedEntityManager().get().find(cqlQuery);
//
//		if (null == mfuList || mfuList.isEmpty()) {
//			log.warn("no MonsterForUserDeleted exists for id=" + userMonsterId);
//			return null;
//		} else if (mfuList.size() > 1) {
//			log.warn("multiple MonsterForUserDeleted exists for id=" + userMonsterId +
//					"\t monsters=" + mfuList + "\t keeping first one");
//			
//			return mfuList.get(0);
//		} else{
//			MonsterForUserDeleted mfu = mfuList.get(0);
//			log.info("retrieved one MonsterForUserDeleted. mfu=" + mfu);
//			return mfu;
//		}
	}
	
	@Override
	public Map<UUID, MonsterForUserDeleted> getSpecificOrAllUserMonstersForUser(UUID userId,
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

		Map<String, Collection<?>> isConditions = null;
		String isCondDelim = null;
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used
		//(its purpose is to hold the values that were supposed to be put
		//into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, isConditions, isCondDelim, inConditions,
				inCondDelim, delimAcrossConditions, values);
		List<MonsterForUserDeleted> mfuList = getMonsterForUserDeletedEntityManager().get().find(cqlQuery);
		
		Map<UUID, MonsterForUserDeleted> userMonsterIdsToUserMonsters =
				new HashMap<UUID, MonsterForUserDeleted>();
		for (MonsterForUserDeleted mfu : mfuList) {
			UUID userMonsterId = mfu.getId();
			userMonsterIdsToUserMonsters.put(userMonsterId, mfu);
		}
		
		return userMonsterIdsToUserMonsters;
	}
	
	@Override
	public Map<Integer, MonsterForUserDeleted> getIncompleteMonstersWithUserAndMonsterIds(
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

		Map<String, Collection<?>> isConditions = null;
		String isCondDelim = null;
		String delimAcrossConditions = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(
				TABLE_NAME, equalityConditions, equalityCondDelim, greaterThanConditions,
				greaterThanCondDelim, isConditions, isCondDelim, inConditions,
				inCondDelim, delimAcrossConditions, values);
		List<MonsterForUserDeleted> mfuList = getMonsterForUserDeletedEntityManager().get().find(cqlQuery);
		
		Map<Integer, MonsterForUserDeleted> monsterIdsToUserMonsters =
				new HashMap<Integer, MonsterForUserDeleted>();
		
		//the reason why monsterIdsToUserMonsters is not a HashMap<Integer, List<MonsterForUserDeleted>>
		//is because, ideally, for each monster of which the user has at least two, only
		//one should be incomplete
		//e.g. if user has 2+ monsters with id=1 then only one of them should be incomplete
		for (MonsterForUserDeleted mfu : mfuList) {
			Integer monsterId = mfu.getMonsterId();
			monsterIdsToUserMonsters.put(monsterId, mfu);
		}
		
		return monsterIdsToUserMonsters;
	}
	
	
	
	//INSERTING STUFF****************************************************************
	@Override
	public void createUserMonsterDeletedFromUserMonsters(String deleteReason,
			Map<UUID, String> details, Date date, Map<UUID, MonsterForUser> mfuMap) {
		List<MonsterForUserDeleted> deleted = new ArrayList<MonsterForUserDeleted>();
		
		for (UUID mfuId : mfuMap.keySet()) {
			MonsterForUser mfu = mfuMap.get(mfuId);
			UUID userId = mfu.getUserId();
			int monsterId = mfu.getMonsterId();
			int curExp = mfu.getCurrentExp();
			int curLvl = mfu.getCurrentLvl();
			int curHealth = mfu.getCurrentHealth();
			int numPieces = mfu.getNumPieces();
			boolean isComplete = mfu.isComplete();
			Date combineStartTime = mfu.getCombineStartTime();
			int teamSlotNum = mfu.getTeamSlotNum();
			String sourceOfPieces = mfu.getSourceOfPieces();
			
			//not sure if need to save the dude before setting more values
			MonsterForUserDeleted mfud = new MonsterForUserDeleted();
			mfud.setId(mfuId);
			mfud.setUserId(userId);
			mfud.setMonsterId(monsterId);
			mfud.setExp(curExp);
			mfud.setLvl(curLvl);
			mfud.setHealth(curHealth);
			mfud.setNumPieces(numPieces);
			mfud.setComplete(isComplete);
			mfud.setCombineStartTime(combineStartTime);
			mfud.setTeamSlotNum(teamSlotNum);
			mfud.setSourceOfPieces(sourceOfPieces);
			
			mfud.setDeletedReason(deleteReason);
			String detail = details.get(mfud);
			mfud.setDetails(detail);
			mfud.setDeletedTime(date);
			
			deleted.add(mfud);
		}
		saveUserMonsters(deleted);
	}
	
	
	
	//SAVING STUFF****************************************************************
	@Override
	public void saveUserMonster(MonsterForUserDeleted mfu) {
		getMonsterForUserDeletedEntityManager().get().put(mfu);
	}
	
	@Override
	public void saveUserMonsters(List<MonsterForUserDeleted> mfuList) {
		log.info("(before) saving mfuList=" + mfuList);
		getMonsterForUserDeletedEntityManager().get().put(mfuList);
		log.info("saved mfuList");
	}
	
	
	//UPDATING STUFF****************************************************************
	
	
	// DELETING STUFF****************************************************************
	
	
	
	//for the setter dependency injection or something****************************************************************
	@Override
	public MonsterForUserDeletedEntityManager getMonsterForUserDeletedEntityManager() {
		return monsterForUserDeletedEntityManager;
	}
	@Override
	public void setMonsterForUserDeletedEntityManager(
			MonsterForUserDeletedEntityManager monsterForUserDeletedEntityManager) {
		this.monsterForUserDeletedEntityManager = monsterForUserDeletedEntityManager;
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
	public Map<UUID, MonsterForUserDeleted> getUserEquipsByUserEquipIds(Collection<UUID> ids) {
		Map<UUID, MonsterForUserDeleted> returnVal = new HashMap<UUID, MonsterForUserDeleted>();
		
		List<MonsterForUserDeleted> ueList = monsterForUserDeletedEntityManager.get().get(ids);
		for (MonsterForUserDeleted ue : ueList) {
			UUID id = ue.getId();
			returnVal.put(id, ue);
		}
		
		return returnVal;
	}
	
	@Override
	public void saveEquips(Collection<MonsterForUserDeleted> newEquips) {
		getMonsterForUserDeletedEntityManager().get().put(newEquips);
	}
	
	@Override
	public void getEquippedUserEquips(List<MonsterForUserDeleted> allUserEquips, List<MonsterForUserDeleted> equippedUserEquips) {
		for(MonsterForUserDeleted ue : allUserEquips) {
			if(ue.isEquipped())
				equippedUserEquips.add(ue);
		}
	}
	
	public  MonsterForUserDeleted getUserEquipForId(UUID id) {
		log.debug("retrieve MonsterForUserDeleted data for id " + id);
		if (idsToUserEquips == null) {
			setStaticIdsToUserEquips();      
		}
		return idsToUserEquips.get(id);
	}

	public  Map<UUID, MonsterForUserDeleted> getUserEquipsForIds(List<UUID> ids) {
		log.debug("retrieve UserEquips data for ids " + ids);
		if (idsToUserEquips == null) {
			setStaticIdsToUserEquips();      
		}
		Map<UUID, MonsterForUserDeleted> toreturn = new HashMap<UUID, MonsterForUserDeleted>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToUserEquips.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToUserEquips() {
		log.debug("setting  map of UserEquipIds to UserEquips");

		String cqlquery = "select * from user_equip;"; 
		List <MonsterForUserDeleted> list = getMonsterForUserDeletedEntityManager().get().find(cqlquery);
		idsToUserEquips = new HashMap<UUID, MonsterForUserDeleted>();
		for(MonsterForUserDeleted us : list) {
			UUID id= us.getId();
			idsToUserEquips.put(id, us);
		}
					
	}

	public  List<MonsterForUserDeleted> getAllUserEquipsForUser(UUID userId) {
		String cqlquery = "select * from user_equip where user_id=" + userId + ";"; 
		List <MonsterForUserDeleted> list = getMonsterForUserDeletedEntityManager().get().find(cqlquery);
		return list;
	}
	
	public StructureLab getEquipmentCorrespondingToUserEquip(MonsterForUserDeleted ue) {
		UUID equipId = ue.getEquipId();
		return getEquipmentRetrieveUtils().getEquipmentForId(equipId);
	}
	
	public List<MonsterForUserDeleted> getAllEquippedUserEquipsForUser(UUID userId) {
		List<MonsterForUserDeleted> ueList = getAllUserEquipsForUser(userId);
		List<MonsterForUserDeleted> equippedList = new ArrayList<>();
		for(MonsterForUserDeleted ue : ueList) {
			if(ue.isEquipped()) {
				equippedList.add(ue);
			}
		}
		return equippedList;
	}
	*/
	
		
}