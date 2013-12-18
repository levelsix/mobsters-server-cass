package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.StructureHospitalEntityManager;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.po.staticdata.StructureHospital;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class StructureHospitalRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureHospital> structIdsToHospitals;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_HOSPITAL;

	@Autowired
	protected StructureHospitalEntityManager structureHospitalEntityManager;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	
	

	public Map<Integer, StructureHospital> getStructIdsToHospitals() {
		log.debug("retrieving all structs data");
		if (structIdsToHospitals == null) {
			setStaticStructIdsToHospitals();
		}
		return structIdsToHospitals;
	}

	public StructureHospital getHospitalForStructId(int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToHospitals == null) {
			setStaticStructIdsToHospitals();      
		}
		return structIdsToHospitals.get(structId);
	}

	public StructureHospital getUpgradedHospitalForStructId(int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToHospitals == null) {
			setStaticStructIdsToHospitals();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getUpgradedStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureHospital upgradedStruct = structIdsToHospitals.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureHospital getPredecessorHospitalForStructId(int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToHospitals == null) {
			setStaticStructIdsToHospitals();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getPredecessorStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureHospital predecessorStruct = structIdsToHospitals.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private  void setStaticStructIdsToHospitals() {
		log.debug("setting  map of structureIds to hospitals");

		//construct the search parameters
		Map<String, Object> equalityConditions = null;

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(TABLE_NAME, equalityConditions, values, preparedStatement);
		List<StructureHospital> list = getStructureHospitalEntityManager().get().find(cqlquery);

		structIdsToHospitals = new HashMap<Integer, StructureHospital>();
		for(StructureHospital c : list) {
			structIdsToHospitals.put(c.getId(), c);
		}

	}


	public  void reload() {
		setStaticStructIdsToHospitals();
	}

	public StructureHospitalEntityManager getStructureHospitalEntityManager() {
		return structureHospitalEntityManager;
	}

	public void setStructureHospitalEntityManager(
			StructureHospitalEntityManager structureHospitalEntityManager) {
		this.structureHospitalEntityManager = structureHospitalEntityManager;
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
