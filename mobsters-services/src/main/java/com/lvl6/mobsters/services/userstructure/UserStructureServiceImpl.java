package com.lvl6.mobsters.services.userstructure;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.UserStructureEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureRetrieveUtils;
import com.lvl6.mobsters.po.UserStructure;
import com.lvl6.mobsters.po.staticdata.Structure;


@Component
public class UserStructureServiceImpl implements UserStructureService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

//	private  Map<UUID, UserStructure> idsToUserStructures;
	
	@Autowired
	protected UserStructureEntityManager userStructureEntityManager;

	@Autowired
	protected StructureEntityManager structureEntityManager;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	
	@Override
	public  UserStructure getUserStructureForId(UUID id) throws Exception {
		log.debug("retrieve UserStructure data for id " + id);
		String usId = id.toString();
		String cqlquery = "select * from user_structure where user_id="+ usId +";"; 
		List<UserStructure> usList = getUserStructureEntityManager().get().find(cqlquery);
		
		if (null == usList || usList.isEmpty()) {
			log.error("unexpected error: no user_structure for" +
					" user_structure_id=" + usId);
			return null;
		}
		if (usList.size() > 1) {
			String msg = "unexpected error: multiple user_structures for" +
					" id=" + usId + ".\t user_structures=" + usList;
			Exception e = new Exception(msg);
			log.error(msg);
			throw e;
		}
		
		return usList.get(0);
	}

	@Override
	public  Map<UUID, UserStructure> getUserStructuresForIds(List<UUID> ids) {
		log.debug("retrieve UserStructures data for ids " + ids);
		Map<UUID, UserStructure> toreturn = new HashMap<UUID, UserStructure>();
//		for (UUID id : ids) {
//			toreturn.put(id,  idsToUserStructures.get(id));
//		}
		return toreturn;
	}

	@Override
	public  List<UserStructure> getAllUserStructuresForUser(UUID userId) {
		String cqlquery = "select * from user_structure where user_id=" + userId + ";"; 
		List <UserStructure> list = getUserStructureEntityManager().get().find(cqlquery);
		return list;
	}
	
	@Override
	public Structure getStructureCorrespondingToUserStructure(UserStructure us) {
		UUID structureId = us.getId();
		return getStructureRetrieveUtils().getStructureForId(structureId);
	}
	
	

	public UserStructureEntityManager getUserStructureEntityManager() {
		return userStructureEntityManager;
	}

	public void setUserStructureEntityManager(
			UserStructureEntityManager userStructureEntityManager) {
		this.userStructureEntityManager = userStructureEntityManager;
	}

	public StructureEntityManager getStructureEntityManager() {
		return structureEntityManager;
	}

	public void setStructureEntityManager(
			StructureEntityManager structureEntityManager) {
		this.structureEntityManager = structureEntityManager;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}


	
	
	
	
	
	
	
}