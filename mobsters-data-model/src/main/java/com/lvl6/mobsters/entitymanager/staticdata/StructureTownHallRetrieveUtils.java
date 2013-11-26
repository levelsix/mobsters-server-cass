package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.StructureTownHall;
import com.lvl6.mobsters.properties.MobstersDbTables;

@Component public class StructureTownHallRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(
			new Object() { }.getClass().getEnclosingClass());

	private  Map<Integer, StructureTownHall> idsToTownHalls;
	private Map<String, Map<Integer, StructureTownHall>> townHallIdsToLevelsToEquips;

	private  final String TABLE_NAME = MobstersDbTables.TABLE_STRUCTURE_TOWN_HALL;

	@Autowired
	protected StructureTownHallEntityManager townHallEntityManager;

	public  StructureTownHall getTownHallForId(Integer id) {
		log.debug("retrieve townHall data for id " + id);
		if (idsToTownHalls == null) {
			setMaps();      
		}
		return idsToTownHalls.get(id);
	}
	
	public Map<Integer, StructureTownHall> getTownHallsForEquipId(Integer townHallId) {
		if (null == townHallIdsToLevelsToEquips) {
			setMaps();
		}
		return townHallIdsToLevelsToEquips.get(townHallId);
	}
	
	public StructureTownHall getTownHallForEquipIdAndLevel(Integer townHallId,
			int townHallLevel) {
		Map<Integer, StructureTownHall> levelsToEquip =
				getTownHallsForEquipId(townHallId);
		if (null == levelsToEquip) {
			return new StructureTownHall();
		} else {
			return levelsToEquip.get(townHallLevel);
		}
	}

	public  Map<Integer, StructureTownHall> getTownHallsForIds(List<Integer> ids) {
		log.debug("retrieve townHalls data for ids " + ids);
		if (idsToTownHalls == null) {
			setMaps();      
		}
		Map<Integer, StructureTownHall> toreturn = new HashMap<Integer, StructureTownHall>();
		for (Integer id : ids) {
			toreturn.put(id,  idsToTownHalls.get(id));
		}
		return toreturn;
	}

	private  void setMaps() {
		log.debug("setting  map of townHallIds to townHalls");

//		String cqlquery = "select * from townHallment;"; 
//		List <StructureTownHall> list =
//				getTownHallEntityManager().get().find(cqlquery);
//		idsToTownHalls = new HashMap<Integer, StructureTownHall>();
//		for(StructureTownHall e : list) {
//			Integer id = e.getId();
//			idsToTownHalls.put(id, e); //some map for (randomId->townHall)
//			
//			
//			//populate map: (townHallId->
//			//					(level->townHall)
//			//				)
//			String townHallName = e.getName();
//			int level = e.getLevel();
//			
//			//get the map containing the different levels of this townHall
//			Map<Integer, StructureTownHall> existing =
//					townHallIdsToLevelsToEquips.get(townHallName);
//			
//			if (null == existing) {
//				//base case: create a new map to store all diff levels
//				//of this townHall
//				existing = new HashMap<Integer, StructureTownHall>();
//				townHallIdsToLevelsToEquips.put(townHallName, existing);
//			}
//			
//			existing.put(level, e);
//		}
	}

	public StructureTownHall getTownHallCorrespondingToId(String townHallIdStr, Integer townHallId) {
//		
//		if (idsToTownHalls == null) {
//			setMaps();      
//		}
//		if (null == townHallId) {
//			townHallId = Integer.fromString(townHallIdStr);
//		}
		
		return idsToTownHalls.get(townHallId);
	}

	public  void reload() {
		setMaps();
	}
	
	

	public StructureTownHallEntityManager getTownHallEntityManager() {
		return townHallEntityManager;
	}

	public void setTownHallEntityManager(
			StructureTownHallEntityManager townHallEntityManager) {
		this.townHallEntityManager = townHallEntityManager;
	}
}
