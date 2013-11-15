package com.lvl6.mobsters.services.userstructure;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.po.Structure;
import com.lvl6.mobsters.po.UserStructure;

public interface UserStructureService {
		
	public abstract UserStructure getUserStructureForId(UUID id) throws Exception;
	
	public abstract Map<UUID, UserStructure> getUserStructuresForIds(List<UUID> ids);
	
	public abstract List<UserStructure> getAllUserStructuresForUser(UUID userId);
	
	public abstract Structure getStructureCorrespondingToUserStructure(UserStructure us);
	
}