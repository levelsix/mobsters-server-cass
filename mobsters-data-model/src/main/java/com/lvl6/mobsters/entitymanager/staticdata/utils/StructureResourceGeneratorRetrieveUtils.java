package com.lvl6.mobsters.entitymanager.staticdata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.StructureResourceGeneratorEntityManager;
import com.lvl6.mobsters.po.staticdata.StructureResourceGenerator;
import com.lvl6.mobsters.properties.MobstersDbTables;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

@Component public class StructureResourceGeneratorRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureResourceGenerator> structIdsToResourceGenerators;
	
	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_RESOURCE_GENERATOR;

	@Autowired
	protected StructureResourceGeneratorEntityManager structureResourceGeneratorEntityManager;

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	
	
	public Map<Integer, StructureResourceGenerator> getStructIdsToResourceGenerators() {
		if (null == structIdsToResourceGenerators) {
			setStaticIdsToStructureResourceGenerators();
		}
		return structIdsToResourceGenerators;
	}

	public StructureResourceGenerator getStructureResourceGeneratorForId(Integer id) {
		log.debug("retrieve StructureResourceGenerator for id " + id);
		if (structIdsToResourceGenerators == null) {
			setStaticIdsToStructureResourceGenerators();      
		}
		return structIdsToResourceGenerators.get(id);
	}

	public  Map<Integer, StructureResourceGenerator> getStructureResourceGeneratorsForIds(List<Integer> ids) {
		log.debug("retrieve StructureResourceGenerator for ids " + ids);
		if (structIdsToResourceGenerators == null) {
			setStaticIdsToStructureResourceGenerators();      
		}
		Map<Integer, StructureResourceGenerator> toreturn = new HashMap<Integer, StructureResourceGenerator>();
		for (Integer id : ids) {
			toreturn.put(id,  structIdsToResourceGenerators.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToStructureResourceGenerators() {
		log.debug("setting  map of resource generator ids to resource generators");

		Map<String, Object> equalityConditions = null;

		//query db, "values" is not used 
		//(its purpose is to hold the values that were supposed to be put
		// into a prepared statement) 
		List<Object> values = new ArrayList<Object>();
		boolean preparedStatement = false;
		String cqlquery = getQueryConstructionUtil().selectRowsQueryEqualityConditions(TABLE_NAME, equalityConditions, values, preparedStatement);
		List<StructureResourceGenerator> list = getStructureResourceGeneratorEntityManager().get().find(cqlquery);
		structIdsToResourceGenerators = new HashMap<Integer, StructureResourceGenerator>();
		for(StructureResourceGenerator s : list) {
			Integer id= s.getId();
			structIdsToResourceGenerators.put(id, s);
		}		
	}
	

	public  void reload() {
		setStaticIdsToStructureResourceGenerators();
	}
	
	

	public StructureResourceGeneratorEntityManager getStructureResourceGeneratorEntityManager() {
		return structureResourceGeneratorEntityManager;
	}

	public void setStructureResourceGeneratorEntityManager(
			StructureResourceGeneratorEntityManager structureResourceGeneratorEntityManager) {
		this.structureResourceGeneratorEntityManager = structureResourceGeneratorEntityManager;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
