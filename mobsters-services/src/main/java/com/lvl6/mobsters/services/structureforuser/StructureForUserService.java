package com.lvl6.mobsters.services.structureforuser;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.StructureForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.po.staticdata.StructureResourceGenerator;
import com.lvl6.mobsters.services.time.TimeUtils;
import com.lvl6.mobsters.utils.CoordinatePair;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface StructureForUserService {
		
	//CONTROLLER LOGIC STUFF****************************************************************
	public abstract List<Date> calculateValidUserStructs(Date clientTime,
			List<StructureForUser> userStructs, List<UUID> validUserStructIds,
			List<StructureForUser> validUserStructs);
	
	public abstract Map<UUID, StructureResourceGenerator> getUserStructIdsToResourceGenerators(
			Collection<StructureForUser> userStructs);
	
	public abstract void collectFromUserStructs(User aUser, List<UUID> userStructIds,
			Map<UUID, StructureForUser> userStructIdsToUserStructs,
			Map<UUID, StructureResourceGenerator> userStructIdsToGenerators,
			Map<UUID, Date> userStructIdsToTimesOfRetrieval,
			Map<UUID, Integer> userStructIdsToAmountCollected,
			Map<String, Integer> resourcesGained);
	
	//RETRIEVING STUFF****************************************************************
	public abstract List<StructureForUser> getAllUserStructuresForUser(UUID userId);
	
	public abstract StructureForUser getSpecificUserStruct(UUID id) throws Exception;
	
	public abstract Map<UUID, StructureForUser> getSpecificOrAllUserStructuresForUser(UUID userId,
			Collection<UUID> userStructureIds);
	
	
	
	//INSERTING STUFF****************************************************************
	public abstract StructureForUser insertUserStruct(UUID userId, int structId,
			 Date lastRetrievedTime, CoordinatePair coords, Date timeOfPurchase,
			boolean isComplete, String orientation);
	
	//SAVING STUFF****************************************************************
	public abstract void saveStructureForUser(StructureForUser sfu);
	
	public abstract void saveStructuresForUser(List<StructureForUser> sfuList);
	
	//UPDATING STUFF****************************************************************
	public abstract void updateUserStructCoordinates(StructureForUser sfu,
			CoordinatePair coordinates);
	
	public abstract void upgradeUserStruct(StructureForUser userStruct, Structure nextLevelStruct,
			Date timeOfUpgrade);
	
	public abstract void updateUserStructBuildingIsComplete(StructureForUser buildDone,
			Date newRetrievedTime);
	
	public abstract void updateUserStructsBuildingIsComplete(List<StructureForUser> buildsDone,
			List<Date> newRetrievedTimes);
	
	public abstract void updateUserStructsLastCollectedTime(
			Map<UUID, Date> userStructIdsToTimesOfRetrieval,
			Map<UUID, StructureForUser> userStructIdsToUserStructs);
	
	//DELETING STUFF****************************************************************
	
	
	
	//for the setter dependency injection or something
	
	public abstract StructureForUserEntityManager getStructureForUserEntityManager();
	
	public abstract void setStructureForUserEntityManager(StructureForUserEntityManager structureForUserEntityManager);
	
	public abstract StructureEntityManager getStructureEntityManager();
	
	public abstract void setStructureEntityManager(StructureEntityManager structureEntityManager);
	
	public abstract StructureRetrieveUtils getStructureRetrieveUtils();
	
	public abstract void setStructureRetrieveUtils(StructureRetrieveUtils structureRetrieveUtils);

	public abstract QueryConstructionUtil getQueryConstructionUtil();

	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);

	public abstract TimeUtils getTimeUtils();
	
	public abstract void setTimeUtils(TimeUtils timeUtils);
	
	public abstract StructureResourceGeneratorRetrieveUtils getStructureResourceGeneratorRetrieveUtils();
	
	public abstract void setStructureResourceGeneratorRetrieveUtils(
			StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils);
	
	//old aoc2 stuff ****************************************************************
	//public abstract Map<UUID, StructureForUser> getUserStructuresForIds(List<UUID> ids);
	
	//public abstract Structure getStructureCorrespondingToUserStructure(StructureForUser us);
}