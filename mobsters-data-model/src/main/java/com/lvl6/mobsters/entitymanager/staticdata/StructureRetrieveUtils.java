package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.Structure;

@Component public class StructureRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<String, Map<Integer, Structure>> structNameToLevelsToStructure;

	private  Map<UUID, Structure> idsToStructures;
	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected StructureEntityManager structureEntityManager;

	public  Structure getStructureForId(UUID id) {
		log.debug("retrieve structure data for id " + id);
		if (idsToStructures == null) {
			setStaticIdsToStructures();      
		}
		return idsToStructures.get(id);
	}

	public  Map<UUID, Structure> getStructuresForIds(List<UUID> ids) {
		log.debug("retrieve structures data for ids " + ids);
		if (idsToStructures == null) {
			setStaticIdsToStructures();      
		}
		Map<UUID, Structure> toreturn = new HashMap<UUID, Structure>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToStructures.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToStructures() {
		log.debug("setting  map of structureIds to structures");

		String cqlquery = "select * from structure;"; 
		List <Structure> list = getStructureEntityManager().get().find(cqlquery);
		structNameToLevelsToStructure = new HashMap<String,Map<Integer, Structure>>();
		for(Structure c : list) {
			idsToStructures.put(c.getId(), c);
			String structureName = c.getName();
			Map<Integer, Structure> innerMap = structNameToLevelsToStructure.get(structureName);
			if(innerMap == null) {
				innerMap = new HashMap<Integer, Structure>();
				structNameToLevelsToStructure.put(structureName, innerMap);
			}
			innerMap.put(c.getLvl(), c);
		}
					
	}
	
	public Structure getUpgradedStructure(Structure s) {
		if(structNameToLevelsToStructure == null) {
			setStaticIdsToStructures();
		}
		Structure upgradedStructure;
		int level = s.getLvl();
		String structureName = s.getName();
		Map<Integer, Structure> sMap = structNameToLevelsToStructure.get(structureName);
		upgradedStructure = sMap.get(level+1);
		if(upgradedStructure != null) {
			return upgradedStructure;
		}
		else return null;
	}
	
	public Structure getStructureWithNameAndLevel(String name, int level) {
		Structure s = new Structure();
		for(Map.Entry<String, Map<Integer, Structure>> entry : structNameToLevelsToStructure.entrySet()) {
			String structureName = entry.getKey();
			Map<Integer, Structure> map = entry.getValue();
			if(name == structureName) {
				for(Map.Entry<Integer, Structure> entry2 : map.entrySet()) {
					if(level == entry2.getKey()) {
						s = entry2.getValue();
					}
				}
			
			}
		}
		return s;
		
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
}
