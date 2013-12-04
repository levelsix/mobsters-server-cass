package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.StructureResourceGenerator;
import com.lvl6.mobsters.properties.MobstersDbTables;

@Component public class StructureResourceGeneratorRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureResourceGenerator> idsToStructureResourceGenerators;
	
	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_RESOURCE_GENERATOR;

	@Autowired
	protected StructureResourceGeneratorEntityManager structureResourceGeneratorEntityManager;
	
	public Map<Integer, StructureResourceGenerator> getStructIdsToResourceGenerators() {
		if (null == idsToStructureResourceGenerators) {
			setStaticIdsToStructureResourceGenerators();
		}
		return idsToStructureResourceGenerators;
	}

	public StructureResourceGenerator getStructureResourceGeneratorForId(Integer id) {
		log.debug("retrieve StructureResourceGenerator for id " + id);
		if (idsToStructureResourceGenerators == null) {
			setStaticIdsToStructureResourceGenerators();      
		}
		return idsToStructureResourceGenerators.get(id);
	}

	public  Map<Integer, StructureResourceGenerator> getStructureResourceGeneratorsForIds(List<Integer> ids) {
		log.debug("retrieve StructureResourceGenerator for ids " + ids);
		if (idsToStructureResourceGenerators == null) {
			setStaticIdsToStructureResourceGenerators();      
		}
		Map<Integer, StructureResourceGenerator> toreturn = new HashMap<Integer, StructureResourceGenerator>();
		for (Integer id : ids) {
			toreturn.put(id,  idsToStructureResourceGenerators.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToStructureResourceGenerators() {
		log.debug("setting  map of consumableIds to consumables");

		String cqlquery = "select * from " + TABLE_NAME; 
		List<StructureResourceGenerator> list = getStructureResourceGeneratorEntityManager().get().find(cqlquery);
		idsToStructureResourceGenerators = new HashMap<Integer, StructureResourceGenerator>();
		for(StructureResourceGenerator s : list) {
			Integer id= s.getId();
			idsToStructureResourceGenerators.put(id, s);
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
}
