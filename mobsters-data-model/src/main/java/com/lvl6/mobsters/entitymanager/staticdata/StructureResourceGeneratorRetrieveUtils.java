package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.StructureResourceGenerator;

@Component public class StructureResourceGeneratorRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureResourceGenerator> idsToSpells;
	
	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected StructureResourceGeneratorEntityManager structureResourceGeneratorEntityManager;

	public  StructureResourceGenerator getSpellForId(Integer id) {
		log.debug("retrieve spell data for id " + id);
		if (idsToSpells == null) {
			setStaticIdsToSpells();      
		}
		return idsToSpells.get(id);
	}

	public  Map<Integer, StructureResourceGenerator> getSpellsForIds(List<Integer> ids) {
		log.debug("retrieve spells data for ids " + ids);
		if (idsToSpells == null) {
			setStaticIdsToSpells();      
		}
		Map<Integer, StructureResourceGenerator> toreturn = new HashMap<Integer, StructureResourceGenerator>();
		for (Integer id : ids) {
			toreturn.put(id,  idsToSpells.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToSpells() {
		log.debug("setting  map of consumableIds to consumables");

		String cqlquery = "select * from spell;"; 
		List <StructureResourceGenerator> list = getSpellEntityManager().get().find(cqlquery);
		idsToSpells = new HashMap<Integer, StructureResourceGenerator>();
		for(StructureResourceGenerator s : list) {
			Integer id= s.getId();
			idsToSpells.put(id, s);
		}		
	}
//	
//	public StructureResourceGenerator getUpgradedSpell(StructureResourceGenerator s) {
//		if(idsToSpells == null) {
//			setStaticIdsToSpells();
//		}
//		for(StructureResourceGenerator value : idsToSpells.values()) {
//			if((value.getName() == s.getName()) && (value.getLvl() == s.getLvl()+1))
//				return value;	
//		}
//		return null;
//		
//	}
//	
//	public StructureResourceGenerator getSpellAccordingToNameAndLevel(String name, int level) {
//		if(idsToSpells == null) {
//			setStaticIdsToSpells();
//		}
//		for(StructureResourceGenerator value : idsToSpells.values()) {
//			if((value.getName() == name) && (value.getLvl() == level))
//				return value;
//		}
//		return null;
//	}
	

	public  void reload() {
		setStaticIdsToSpells();
	}
	
	

	public StructureResourceGeneratorEntityManager getSpellEntityManager() {
		return structureResourceGeneratorEntityManager;
	}

	public void setSpellEntityManager(
			StructureResourceGeneratorEntityManager structureResourceGeneratorEntityManager) {
		this.structureResourceGeneratorEntityManager = structureResourceGeneratorEntityManager;
	}
}
