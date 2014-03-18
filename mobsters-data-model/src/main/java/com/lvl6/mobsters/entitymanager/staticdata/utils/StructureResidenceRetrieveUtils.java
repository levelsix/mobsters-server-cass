package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.StructureResidenceEntityManager;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.po.staticdata.StructureResidence;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class StructureResidenceRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureResidence> structIdsToResidences;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_RESIDENCE;

	@Autowired
	protected StructureResidenceEntityManager structureResidenceEntityManager;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

	
	
	
	public Map<Integer, StructureResidence> getStructIdsToResidences() {
		log.debug("retrieving all structs data");
		if (structIdsToResidences == null) {
			setStaticStructIdsToResidences();
		}
		return structIdsToResidences;
	}

	public StructureResidence getResidenceForStructId(int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToResidences == null) {
			setStaticStructIdsToResidences();      
		}
		return structIdsToResidences.get(structId);
	}

	public StructureResidence getUpgradedResidenceForStructId(int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToResidences == null) {
			setStaticStructIdsToResidences();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getUpgradedStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureResidence upgradedStruct = structIdsToResidences.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureResidence getPredecessorResidenceForStructId(int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToResidences == null) {
			setStaticStructIdsToResidences();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getPredecessorStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureResidence predecessorStruct = structIdsToResidences.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}
	

	private void setStaticStructIdsToResidences() {
		//log.debug("setting  map of structureIds to town halls");		

		//construct the search parameters
		Map<String, Object> equalityConditions = null;
		String conditionDelimiter = getQueryConstructionUtil().getAnd();
		
		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		boolean allowFiltering = false; //don't let cassandra query with non row keys
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
				TABLE_NAME, equalityConditions, conditionDelimiter, values,
				preparedStatement, allowFiltering);
		List<StructureResidence> list = getResidenceEntityManager().get().find(cqlquery);

		structIdsToResidences = new HashMap<Integer, StructureResidence>();
		for(StructureResidence c : list) {
			structIdsToResidences.put(c.getId(), c);
		}
	}
	
	
	public void reload() {
		setStaticStructIdsToResidences();
	}
	
	

	public StructureResidenceEntityManager getResidenceEntityManager() {
		return structureResidenceEntityManager;
	}

	public void setResidenceEntityManager(
			StructureResidenceEntityManager structureResidenceEntityManager) {
		this.structureResidenceEntityManager = structureResidenceEntityManager;
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
