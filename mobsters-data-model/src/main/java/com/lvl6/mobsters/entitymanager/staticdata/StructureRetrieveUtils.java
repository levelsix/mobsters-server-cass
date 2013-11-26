package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class StructureRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, Structure> idsToStructures;
	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE;

	
	
	@Autowired
	protected StructureEntityManager structureEntityManager;
	
	@Autowired
	private QueryConstructionUtil queryConstructionUtil;
	

	public  Structure getStructureForId(Integer id) {
		log.debug("retrieve structure data for id " + id);
		if (idsToStructures == null) {
			setStaticIdsToStructures();      
		}
		return idsToStructures.get(id);
	}

	public  Map<Integer, Structure> getStructuresForIds(List<Integer> ids) {
		log.debug("retrieve structures data for ids " + ids);
		if (idsToStructures == null) {
			setStaticIdsToStructures();      
		}
		Map<Integer, Structure> toreturn = new HashMap<Integer, Structure>();
		for (Integer id : ids) {
			toreturn.put(id,  idsToStructures.get(id));
		}
		return toreturn;
	}
	
	public Structure getUpgradedStructure(Structure currentStructure, int structId) {
		if(idsToStructures == null) {
			setStaticIdsToStructures();
		}
		if (null == currentStructure) {
			currentStructure = idsToStructures.get(structId);
		}
		int upgradedStructId = currentStructure.getSuccessorStructId();
		
		Structure upgradedStructure = null;
		//gotta watch out for structures with id of 0 or less.
		if (upgradedStructId > 0 &&idsToStructures.containsKey(upgradedStructId)) {
			upgradedStructure = idsToStructures.get(upgradedStructId);
		}
		
		return upgradedStructure;
	}
	
	public Structure getPredecessorStructure(Structure currentStructure, int structId) {
		if(idsToStructures == null) {
			setStaticIdsToStructures();
		}
		if (null == currentStructure) {
			currentStructure = idsToStructures.get(structId);
		}
		int predecessorStructId = currentStructure.getPredecessorStructId();
		
		Structure predecessorStructure = null;
		//gotta watch out for structures with id of 0 or less.
		if (predecessorStructId > 0 &&idsToStructures.containsKey(predecessorStructId)) {
			predecessorStructure = idsToStructures.get(predecessorStructId);
		}
		
		return predecessorStructure;
	}

	private  void setStaticIdsToStructures() {
		log.debug("setting  map of structureIds to structures");

		//construct the search parameters
		Map<String, Object> equalityConditions = null;

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(TABLE_NAME, equalityConditions, values, preparedStatement);
		List <Structure> list = getStructureEntityManager().get().find(cqlquery);
		
		idsToStructures = new HashMap<Integer, Structure>();
		for(Structure c : list) {
			idsToStructures.put(c.getId(), c);
		}
					
	}
	
	


	public  void reload() {
		setStaticIdsToStructures();
	}
	
	

	public StructureEntityManager getStructureEntityManager() {
		return structureEntityManager;
	}

	public void setStructureEntityManager(
			StructureEntityManager structureEntityManager) {
		this.structureEntityManager = structureEntityManager;
	}
	
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
