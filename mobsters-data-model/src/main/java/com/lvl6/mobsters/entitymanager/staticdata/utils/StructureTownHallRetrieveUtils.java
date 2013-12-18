package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.StructureTownHallEntityManager;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.po.staticdata.StructureTownHall;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class StructureTownHallRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(
			new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureTownHall> structIdsToTownHalls;
	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_TOWN_HALL;

	@Autowired
	protected StructureTownHallEntityManager townHallEntityManager;
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	public Map<Integer, StructureTownHall> getStructIdsToTownHalls() {
		log.debug("retrieving all structs data");
		if (structIdsToTownHalls == null) {
			setStaticStructIdsToTownHalls();
		}
		return structIdsToTownHalls;
	}

//	public StructureTownHall getTownHallRequiredForStructId(int structId) {
//		log.debug("retrieve struct data for structId " + structId);
//		if (structIdsToTownHalls == null) {
//			setStaticStructIdsToTownHalls();      
//		}
//		return structIdsToTownHalls.get(structId);
//	}

	public StructureTownHall getUpgradedTownHallForStructId(int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToTownHalls == null) {
			setStaticStructIdsToTownHalls();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getUpgradedStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureTownHall upgradedStruct = structIdsToTownHalls.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureTownHall getPredecessorTownHallForStructId(int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToTownHalls == null) {
			setStaticStructIdsToTownHalls();      
		}
		Structure currentStructure = null;
		Structure curStruct = getStructureRetrieveUtils().getPredecessorStructure(
				currentStructure, structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureTownHall predecessorStruct = structIdsToTownHalls.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}
	
	private void setStaticStructIdsToTownHalls() {
		log.debug("setting  map of structureIds to town halls");

		//construct the search parameters
		Map<String, Object> equalityConditions = null;

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(TABLE_NAME, equalityConditions, values, preparedStatement);
		List<StructureTownHall> list = getTownHallEntityManager().get().find(cqlquery);
		
		structIdsToTownHalls = new HashMap<Integer, StructureTownHall>();
		for(StructureTownHall c : list) {
			structIdsToTownHalls.put(c.getId(), c);
		}
					
	}

	public void reload() {
		setStaticStructIdsToTownHalls();
	}
	
	

	public StructureTownHallEntityManager getTownHallEntityManager() {
		return townHallEntityManager;
	}

	public void setTownHallEntityManager(
			StructureTownHallEntityManager townHallEntityManager) {
		this.townHallEntityManager = townHallEntityManager;
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
