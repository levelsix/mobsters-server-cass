package com.lvl6.mobsters.services.structureforuser;


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

import com.lvl6.mobsters.entitymanager.nonstaticdata.StructureForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.services.time.TimeUtils;
import com.lvl6.mobsters.utils.CoordinatePair;
import com.lvl6.mobsters.utils.QueryConstructionUtil;


@Component
public class StructureForUserServiceImpl implements StructureForUserService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_FOR_USER;
	
	@Autowired
	protected StructureForUserEntityManager structureForUserEntityManager;

	@Autowired
	protected StructureEntityManager structureEntityManager;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected TimeUtils timeUtils;
	
	//CONTROLLER LOGIC STUFF****************************************************************
	@Override
	public List<Date> calculateValidUserStructs(Date clientTime,
			List<StructureForUser> userStructs, List<UUID> validUserStructIds,
			List<StructureForUser> validUserStructs) {
		List<Date> timesBuildsFinished = new ArrayList<Date>();
		Map<Integer, Structure> structures = getStructureRetrieveUtils().getStructIdsToStructs();
		
		//for each structure for user, see if it can be considered valid and complete
		//and keep track of the time;
		for (StructureForUser us : userStructs) {
			int structId = us.getStructureId();
			Structure struct = structures.get(structId);
			if (null == struct) {
				log.warn("no struct in db exists with id " + us.getStructureId() +  "\t userStructure=" + us);
		        continue;
			}
			
			Date purchaseDate = us.getPurchaseTime();
			long buildTimeMillis = 60000 * struct.getBuildTimeMinutes();
			if (null == purchaseDate) {
				log.warn("user struct has never been bought or purchased according to db. " + us);
				continue;
			}
			
			Date timeBuildFinishes = getTimeUtils().addMillisToDate(purchaseDate, buildTimeMillis);
			if (getTimeUtils().isFirstEarlierThanSecond(clientTime, timeBuildFinishes)) {
				log.warn("the building is not done yet. userstruct=" + us + ", client time is " +
						clientTime + ", purchase time was " + purchaseDate);
				continue;
			}//else this building is done now
			
			validUserStructIds.add(us.getId());
			validUserStructs.add(us);
			timesBuildsFinished.add(timeBuildFinishes);
		}
		return timesBuildsFinished;
	}

	//RETRIEVING STUFF****************************************************************
	@Override
	public  List<StructureForUser> getAllUserStructuresForUser(UUID userId) {
		log.debug("retrieve StructureForUser data for user with id " + userId);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.STRUCTURE_FOR_USER__USER_ID, userId);
		
		//query db, "values" is not used
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, values, preparedStatement);
		List<StructureForUser> list = getStructureForUserEntityManager().get().find(cqlquery);
		return list;
	}
	
	@Override
	public  StructureForUser getSpecificUserStruct(UUID id) {
		log.debug("retrieve StructureForUser data for id " + id);

		StructureForUser sfu = getStructureForUserEntityManager().get().get(id);
		return sfu;
//		//construct the search parameters
//		Map<String, Object> equalityConditions = new HashMap<String, Object>();
//		equalityConditions.put(MobstersDbTables.STRUCTURE_FOR_USER__ID, id);
//
//		//query db, "values" is not used 
//		//(its purpose is to hold the values that were supposed to be put
//		// into a prepared statement)
//		List<Object> values = new ArrayList<Object>();
//		boolean preparedStatement = false;
//		String cqlQuery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
//				TABLE_NAME, equalityConditions, values, preparedStatement);
//		List<StructureForUser> sfuList = getStructureForUserEntityManager().get().find(cqlQuery);
//
//		if (null == sfuList || sfuList.isEmpty()) {
//			log.warn("no StructureForUser exists for id=" + id);
//			return null;
//		} else if (sfuList.size() > 1) {
//			log.warn("multiple StructureForUser exists for id=" + id +
//					"\t structures=" + sfuList + "\t keeping first one");
//			
//			return sfuList.get(0);
//		} else{
//			StructureForUser sfu = sfuList.get(0);
//			log.info("retrieved one StructureForUser. sfu=" + sfu);
//			return sfu;
//		}
	}
	
	@Override
	public Map<UUID, StructureForUser> getSpecificOrAllUserStructuresForUser(UUID userId,
			Collection<UUID> userStructureIds) {
		log.debug("retrieving userStructures for userId=" + userId + "\t userStructureIds=" +
			userStructureIds);
		
		//construct the search parameters
		Map<String, Object> equalityConditions = new HashMap<String, Object>();
		equalityConditions.put(MobstersDbTables.STRUCTURE_FOR_USER__USER_ID, userId);
		Map<String, Object> greaterThanConditions = null;
		Map<String, Collection<?>> inConditions = null;
		if(null != userStructureIds && !userStructureIds.isEmpty()) {
			inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(MobstersDbTables.STRUCTURE_FOR_USER__ID, userStructureIds);
		}
		
		//query db, "values" is not used
		//(its purpose is to hold the values that were supposed to be put
		//into a prepared statement)
		List<Object> values = new ArrayList<Object>();
		String cqlQuery = getQueryConstructionUtil().selectRowsQueryAllConditions(TABLE_NAME,
				equalityConditions, greaterThanConditions, inConditions, values);
		List<StructureForUser> sfuList = getStructureForUserEntityManager().get().find(cqlQuery);
		
		Map<UUID, StructureForUser> userStructureIdsToUserStructures =
				new HashMap<UUID, StructureForUser>();
		for (StructureForUser sfu : sfuList) {
			UUID userStructureId = sfu.getId();
			userStructureIdsToUserStructures.put(userStructureId, sfu);
		}
		
		return userStructureIdsToUserStructures;
	}
	


	//INSERTING STUFF****************************************************************
	@Override
	public StructureForUser insertUserStruct(UUID userId, int structId,
			 Date lastRetrievedTime, CoordinatePair coords, Date timeOfPurchase,
			boolean isComplete, String orientation) {
		StructureForUser sfu = new StructureForUser();
		
		sfu.setUserId(userId);
		sfu.setStructureId(structId);
		sfu.setLastCollectTime(lastRetrievedTime);
		float xCoordinate = coords.getX();
		sfu.setxCoordinate(xCoordinate);
		float yCoordinate = coords.getY();
		sfu.setyCoordinate(yCoordinate);
		sfu.setPurchaseTime(timeOfPurchase);
		sfu.setComplete(isComplete);
		sfu.setStructOrientation(orientation);
		
		saveStructureForUser(sfu);
		return sfu;
	}

	//SAVING STUFF****************************************************************
	@Override
	public void saveStructureForUser(StructureForUser sfu) {
		getStructureForUserEntityManager().get().put(sfu);
	}
	
	@Override
	public void saveStructuresForUser(List<StructureForUser> sfuList) {
		getStructureForUserEntityManager().get().put(sfuList);
	}
	
	//UPDATING STUFF****************************************************************
	@Override
	public void updateUserStructCoordinates(StructureForUser sfu, CoordinatePair coordinates) {
		float x = coordinates.getX();
		float y = coordinates.getY();
		
		sfu.setxCoordinate(x);
		sfu.setyCoordinate(y);
		
		saveStructureForUser(sfu);
	}
	
	@Override
	public void upgradeUserStruct(StructureForUser userStruct, Structure nextLevelStruct,
			Date timeOfUpgrade) {
		if (null == nextLevelStruct) {
			Structure currentStructure = null;
			int structId = userStruct.getStructureId();
			nextLevelStruct = getStructureRetrieveUtils().getUpgradedStructure(currentStructure, structId);
		}
		int nextLevelStructId = nextLevelStruct.getId();
		
		userStruct.setStructureId(nextLevelStructId);
		userStruct.setPurchaseTime(timeOfUpgrade);
		userStruct.setComplete(false);
		
		saveStructureForUser(userStruct);
	}
	
	@Override
	public void updateUserStructBuildingIsComplete(StructureForUser buildDone,
			Date newRetrievedTime) {
		buildDone.setLastCollectTime(newRetrievedTime);
		buildDone.setComplete(true);
		saveStructureForUser(buildDone);
	}

	@Override
	public void updateUserStructsBuildingIsComplete(List<StructureForUser> buildsDone,
			List<Date> newRetrievedTimes) {
		boolean isComplete = true;
		for (int index = 0; index < buildsDone.size(); index++) {
			StructureForUser userStruct = buildsDone.get(index);
			Date newRetrievedTime = newRetrievedTimes.get(index);
			
			userStruct.setLastCollectTime(newRetrievedTime);
			userStruct.setComplete(isComplete);
		}
		saveStructuresForUser(buildsDone);
	}
	
	//DELETING STUFF****************************************************************
	
	
	
	//for the setter dependency injection or something
	@Override
	public StructureForUserEntityManager getStructureForUserEntityManager() {
		return structureForUserEntityManager;
	}
	@Override
	public void setStructureForUserEntityManager(
			StructureForUserEntityManager structureForUserEntityManager) {
		this.structureForUserEntityManager = structureForUserEntityManager;
	}
	@Override
	public StructureEntityManager getStructureEntityManager() {
		return structureEntityManager;
	}
	@Override
	public void setStructureEntityManager(
			StructureEntityManager structureEntityManager) {
		this.structureEntityManager = structureEntityManager;
	}
	@Override
	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}
	@Override
	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
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
	public TimeUtils getTimeUtils() {
		return timeUtils;
	}
	@Override
	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}
	

	//old aoc2 stuff****************************************************************
	/*@Override
	public  Map<UUID, StructureForUser> getUserStructuresForIds(List<UUID> ids) {
		log.debug("retrieve UserStructures data for ids " + ids);
		Map<UUID, StructureForUser> toreturn = new HashMap<UUID, StructureForUser>();
//		for (UUID id : ids) {
//			toreturn.put(id,  idsToUserStructures.get(id));
//		}
		return toreturn;
	}

	@Override
	public Structure getStructureCorrespondingToUserStructure(StructureForUser us) {
		int structureId = us.getStructureId();
		return getStructureRetrieveUtils().getStructureForId(structureId);
	}*/
	
}
