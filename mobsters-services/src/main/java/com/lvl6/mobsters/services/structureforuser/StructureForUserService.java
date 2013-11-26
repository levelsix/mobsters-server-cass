package com.lvl6.mobsters.services.structureforuser;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.StructureForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface StructureForUserService {
		
	//CONTROLLER LOGIC STUFF
	
	//RETRIEVING STUFF
	public abstract List<StructureForUser> getAllUserStructuresForUser(UUID userId);
	
	public abstract StructureForUser getSpecificUserStruct(UUID id) throws Exception;
	
	public abstract Map<UUID, StructureForUser> getSpecificOrAllUserStructuresForUser(UUID userId,
			Collection<UUID> userStructureIds);
	
	//public abstract Map<UUID, StructureForUser> getUserStructuresForIds(List<UUID> ids);
	
	//public abstract Structure getStructureCorrespondingToUserStructure(StructureForUser us);
	
	
	//INSERTING STUFF
	
	//UPDATING STUFF
	
	//DELETING STUFF
	
	
	
	//for the setter dependency injection or something
	
	public abstract StructureForUserEntityManager getStructureForUserEntityManager();
	
	public abstract void setStructureForUserEntityManager(StructureForUserEntityManager structureForUserEntityManager);
	
	public abstract StructureEntityManager getStructureEntityManager();
	
	public abstract void setStructureEntityManager(StructureEntityManager structureEntityManager);
	
	public abstract StructureRetrieveUtils getStructureRetrieveUtils();
	
	public abstract void setStructureRetrieveUtils(StructureRetrieveUtils structureRetrieveUtils);

	public abstract QueryConstructionUtil getQueryConstructionUtil();

	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);

}