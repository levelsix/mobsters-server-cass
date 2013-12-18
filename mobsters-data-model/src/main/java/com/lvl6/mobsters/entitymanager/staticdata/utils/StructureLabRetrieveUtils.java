package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.StructureLabEntityManager;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.po.staticdata.StructureLab;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class StructureLabRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(
			new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, StructureLab> structIdsToLabs;
	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_LAB;

	@Autowired
	protected StructureLabEntityManager structureLabEntityManager;

	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	

	public Map<Integer, StructureLab> getStructIdsToLabs() {
		log.debug("retrieving all structs data");
		if (structIdsToLabs == null) {
			setStaticStructIdsToLabs();
		}
		return structIdsToLabs;
	}

	public StructureLab getLabForStructId(int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToLabs == null) {
			setStaticStructIdsToLabs();      
		}
		return structIdsToLabs.get(structId);
	}

	public StructureLab getUpgradedLabForStructId(int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToLabs == null) {
			setStaticStructIdsToLabs();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getUpgradedStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureLab upgradedStruct = structIdsToLabs.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureLab getPredecessorLabForStructId(int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToLabs == null) {
			setStaticStructIdsToLabs();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getPredecessorStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureLab predecessorStruct = structIdsToLabs.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private void setStaticStructIdsToLabs() {
		//log.debug("setting  map of structureIds to town halls");		

		//construct the search parameters
		Map<String, Object> equalityConditions = null;

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(TABLE_NAME, equalityConditions, values, preparedStatement);
		List<StructureLab> list = getStructureLabEntityManager().get().find(cqlquery);

		structIdsToLabs = new HashMap<Integer, StructureLab>();
		for(StructureLab c : list) {
			structIdsToLabs.put(c.getId(), c);
		}
	}
	

	public  void reload() {
		setStaticStructIdsToLabs();
	}

	public StructureLabEntityManager getStructureLabEntityManager() {
		return structureLabEntityManager;
	}

	public void setStructureLabEntityManager(
			StructureLabEntityManager structureLabEntityManager) {
		this.structureLabEntityManager = structureLabEntityManager;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	

}
