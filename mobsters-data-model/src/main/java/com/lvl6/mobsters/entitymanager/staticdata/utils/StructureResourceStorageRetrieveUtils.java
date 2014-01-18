package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.StructureResourceStorageEntityManager;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.po.staticdata.StructureResourceStorage;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class StructureResourceStorageRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureResourceStorage> structIdsToResourceStorages;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_RESOURCE_STORAGE;

	@Autowired
	protected StructureResourceStorageEntityManager resourceStorageEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	public Map<Integer, StructureResourceStorage> getStructIdsToResourceStorages() {
		log.debug("retrieving all structs data");
		if (structIdsToResourceStorages == null) {
			setStaticStructIdsToResourceStorages();
		}
		return structIdsToResourceStorages;
	}

	public StructureResourceStorage getResourceStorageForStructId(int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToResourceStorages == null) {
			setStaticStructIdsToResourceStorages();      
		}
		return structIdsToResourceStorages.get(structId);
	}

	public StructureResourceStorage getUpgradedResourceStorageForStructId(int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToResourceStorages == null) {
			setStaticStructIdsToResourceStorages();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getUpgradedStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureResourceStorage upgradedStruct = structIdsToResourceStorages.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureResourceStorage getPredecessorResourceStorageForStructId(int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToResourceStorages == null) {
			setStaticStructIdsToResourceStorages();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getPredecessorStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureResourceStorage predecessorStruct = structIdsToResourceStorages.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private void setStaticStructIdsToResourceStorages() {
		log.debug("setting static map of structIds to structs");

		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values, preparedStatement);
		List<StructureResourceStorage> list = getResourceStorageEntityManager().get().find(cqlquery);
		
		structIdsToResourceStorages = new HashMap<Integer, StructureResourceStorage>();
		for (StructureResourceStorage srs : list) {
			//ensuring that enum string is stripped of white space and capitalized
			String typeStr = srs.getResourceType();
			String newTypeStr = typeStr.trim().toUpperCase(Locale.ENGLISH);
			if (!typeStr.equals(newTypeStr)) {
				log.error("struct resource storage resource type incorrectly set. srs=" + srs);
			}
			srs.setResourceType(newTypeStr);
			
			structIdsToResourceStorages.put(srs.getId(), srs);
		}
	}



	public void reload() {
		setStaticStructIdsToResourceStorages();
	}

	public StructureResourceStorageEntityManager getResourceStorageEntityManager() {
		return resourceStorageEntityManager;
	}

	public void setResourceStorageEntityManager(
			StructureResourceStorageEntityManager resourceStorageEntityManager) {
		this.resourceStorageEntityManager = resourceStorageEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}
	
}
