package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.CombatRoomEntityManager;
import com.lvl6.mobsters.po.CombatRoom;

@Component public class CombatRoomRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, CombatRoom> idsToCombatRooms;

	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected CombatRoomEntityManager combatRoomEntityManager;

	public  CombatRoom getCombatRoomForId(UUID id) {
		log.debug("retrieve combatRoom data for id " + id);
		if (idsToCombatRooms == null) {
			setStaticIdsToCombatRooms();      
		}
		return idsToCombatRooms.get(id);
	}

	public  Map<UUID, CombatRoom> getCombatRoomsForIds(List<UUID> ids) {
		log.debug("retrieve combatRooms data for ids " + ids);
		if (idsToCombatRooms == null) {
			setStaticIdsToCombatRooms();      
		}
		Map<UUID, CombatRoom> toreturn = new HashMap<UUID, CombatRoom>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToCombatRooms.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToCombatRooms() {
		log.debug("setting  map of combatRoomIds to combatRooms");

		String cqlquery = "select * from combatRoom;"; 
		List <CombatRoom> list = getCombatRoomEntityManager().get().find(cqlquery);
		idsToCombatRooms = new HashMap<UUID, CombatRoom>();
		for(CombatRoom c : list) {
			UUID id= c.getId();
			idsToCombatRooms.put(id, c);
		}
	}

	public CombatRoom getCombatRoomForName(String dungeonRoomName) {
		String cqlquery = "select * from combatRoom where name=" + dungeonRoomName + ";";
		List<CombatRoom> list = getCombatRoomEntityManager().get().find(cqlquery);
		return list.get(0);
	}
	
	public List<CombatRoom> getCombatRoomUnlockedAtLevel(int level) {
		String cqlquery = "select * from combatRoom where lvl_required=" + level + ";";
		List<CombatRoom> list = getCombatRoomEntityManager().get().find(cqlquery);
		return list;
	}

	
	public  void reload() {
		setStaticIdsToCombatRooms();
	}
	

	public CombatRoomEntityManager getCombatRoomEntityManager() {
		return combatRoomEntityManager;
	}

	public void setCombatRoomEntityManager(
			CombatRoomEntityManager combatRoomEntityManager) {
		this.combatRoomEntityManager = combatRoomEntityManager;
	}
}
