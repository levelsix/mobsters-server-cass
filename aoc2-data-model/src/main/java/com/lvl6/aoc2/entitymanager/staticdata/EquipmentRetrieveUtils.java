package com.lvl6.aoc2.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.EquipmentEntityManager;
import com.lvl6.aoc2.po.Equipment;

@Component public class EquipmentRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(
			new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, Equipment> idsToEquipments;
	private Map<String, Map<Integer, Equipment>> equipIdsToLevelsToEquips;

	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected EquipmentEntityManager equipEntityManager;

	public  Equipment getEquipmentForId(UUID id) {
		log.debug("retrieve equip data for id " + id);
		if (idsToEquipments == null) {
			setMaps();      
		}
		return idsToEquipments.get(id);
	}
	
	public Map<Integer, Equipment> getEquipmentsForEquipId(UUID equipId) {
		if (null == equipIdsToLevelsToEquips) {
			setMaps();
		}
		return equipIdsToLevelsToEquips.get(equipId);
	}
	
	public Equipment getEquipmentForEquipIdAndLevel(UUID equipId,
			int equipLevel) {
		Map<Integer, Equipment> levelsToEquip =
				getEquipmentsForEquipId(equipId);
		if (null == levelsToEquip) {
			return new Equipment();
		} else {
			return levelsToEquip.get(equipLevel);
		}
	}

	public  Map<UUID, Equipment> getEquipmentsForIds(List<UUID> ids) {
		log.debug("retrieve equips data for ids " + ids);
		if (idsToEquipments == null) {
			setMaps();      
		}
		Map<UUID, Equipment> toreturn = new HashMap<UUID, Equipment>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToEquipments.get(id));
		}
		return toreturn;
	}

	private  void setMaps() {
		log.debug("setting  map of equipIds to equips");

		String cqlquery = "select * from equipment;"; 
		List <Equipment> list =
				getEquipmentEntityManager().get().find(cqlquery);
		idsToEquipments = new HashMap<UUID, Equipment>();
		for(Equipment e : list) {
			UUID id = e.getId();
			idsToEquipments.put(id, e); //some map for (randomId->equip)
			
			
			//populate map: (equipId->
			//					(level->equip)
			//				)
			String equipName = e.getName();
			int level = e.getLevel();
			
			//get the map containing the different levels of this equip
			Map<Integer, Equipment> existing =
					equipIdsToLevelsToEquips.get(equipName);
			
			if (null == existing) {
				//base case: create a new map to store all diff levels
				//of this equip
				existing = new HashMap<Integer, Equipment>();
				equipIdsToLevelsToEquips.put(equipName, existing);
			}
			
			existing.put(level, e);
		}
	}

	public Equipment getEquipmentCorrespondingToId(String equipIdStr, UUID equipId) {
		if (idsToEquipments == null) {
			setMaps();      
		}
		if (null == equipId) {
			equipId = UUID.fromString(equipIdStr);
		}
		
		return idsToEquipments.get(equipId);
	}

	public  void reload() {
		setMaps();
	}
	
	

	public EquipmentEntityManager getEquipmentEntityManager() {
		return equipEntityManager;
	}

	public void setEquipmentEntityManager(
			EquipmentEntityManager equipEntityManager) {
		this.equipEntityManager = equipEntityManager;
	}
}
