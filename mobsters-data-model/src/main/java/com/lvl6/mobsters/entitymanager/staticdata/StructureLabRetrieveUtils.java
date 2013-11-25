package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.StructureLab;
import com.lvl6.mobsters.properties.MobstersDbTables;

@Component public class StructureLabRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(
			new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureLab> idsToEquipments;
	private Map<String, Map<Integer, StructureLab>> equipIdsToLevelsToEquips;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_LAB;

	@Autowired
	protected StructureLabEntityManager equipEntityManager;

	public  StructureLab getEquipmentForId(Integer id) {
		log.debug("retrieve equip data for id " + id);
		if (idsToEquipments == null) {
			setMaps();      
		}
		return idsToEquipments.get(id);
	}
	
	public Map<Integer, StructureLab> getEquipmentsForEquipId(Integer equipId) {
		if (null == equipIdsToLevelsToEquips) {
			setMaps();
		}
		return equipIdsToLevelsToEquips.get(equipId);
	}
	
	public StructureLab getEquipmentForEquipIdAndLevel(Integer equipId,
			int equipLevel) {
		Map<Integer, StructureLab> levelsToEquip =
				getEquipmentsForEquipId(equipId);
		if (null == levelsToEquip) {
			return new StructureLab();
		} else {
			return levelsToEquip.get(equipLevel);
		}
	}

	public  Map<Integer, StructureLab> getEquipmentsForIds(List<Integer> ids) {
		log.debug("retrieve equips data for ids " + ids);
		if (idsToEquipments == null) {
			setMaps();      
		}
		Map<Integer, StructureLab> toreturn = new HashMap<Integer, StructureLab>();
		for (Integer id : ids) {
			toreturn.put(id,  idsToEquipments.get(id));
		}
		return toreturn;
	}

	private  void setMaps() {
		log.debug("setting  map of equipIds to equips");

//		String cqlquery = "select * from equipment;"; 
//		List <StructureLab> list =
//				getEquipmentEntityManager().get().find(cqlquery);
//		idsToEquipments = new HashMap<Integer, StructureLab>();
//		for(StructureLab e : list) {
//			Integer id = e.getId();
//			idsToEquipments.put(id, e); //some map for (randomId->equip)
//			
//			
//			//populate map: (equipId->
//			//					(level->equip)
//			//				)
//			String equipName = e.getName();
//			int level = e.getLevel();
//			
//			//get the map containing the different levels of this equip
//			Map<Integer, StructureLab> existing =
//					equipIdsToLevelsToEquips.get(equipName);
//			
//			if (null == existing) {
//				//base case: create a new map to store all diff levels
//				//of this equip
//				existing = new HashMap<Integer, StructureLab>();
//				equipIdsToLevelsToEquips.put(equipName, existing);
//			}
//			
//			existing.put(level, e);
//		}
	}

	public StructureLab getEquipmentCorrespondingToId(String equipIdStr, Integer equipId) {
//		
//		if (idsToEquipments == null) {
//			setMaps();      
//		}
//		if (null == equipId) {
//			equipId = Integer.fromString(equipIdStr);
//		}
		
		return idsToEquipments.get(equipId);
	}

	public  void reload() {
		setMaps();
	}
	
	

	public StructureLabEntityManager getEquipmentEntityManager() {
		return equipEntityManager;
	}

	public void setEquipmentEntityManager(
			StructureLabEntityManager equipEntityManager) {
		this.equipEntityManager = equipEntityManager;
	}
}
