package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.staticdata.TaskStageMonster;

@Component public class TaskStageMonsterRetrieveUtils {

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, TaskStageMonster> idsToCombatRooms;

	//private  final String TABLE_NAME = DBConstants.CONSUMABLE;

	@Autowired
	protected TaskStageMonsterEntityManager taskStageMonsterEntityManager;

	public  TaskStageMonster getCombatRoomForId(UUID id) {
		log.debug("retrieve combatRoom data for id " + id);
		if (idsToCombatRooms == null) {
			setStaticIdsToCombatRooms();      
		}
		return idsToCombatRooms.get(id);
	}

	public  Map<UUID, TaskStageMonster> getCombatRoomsForIds(List<UUID> ids) {
		log.debug("retrieve combatRooms data for ids " + ids);
		if (idsToCombatRooms == null) {
			setStaticIdsToCombatRooms();      
		}
		Map<UUID, TaskStageMonster> toreturn = new HashMap<UUID, TaskStageMonster>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToCombatRooms.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToCombatRooms() {
		log.debug("setting  map of combatRoomIds to combatRooms");

		String cqlquery = "select * from combatRoom;"; 
		List <TaskStageMonster> list = getCombatRoomEntityManager().get().find(cqlquery);
		idsToCombatRooms = new HashMap<UUID, TaskStageMonster>();
		for(TaskStageMonster c : list) {
			UUID id= c.getId();
			idsToCombatRooms.put(id, c);
		}
	}

	public TaskStageMonster getCombatRoomForName(String dungeonRoomName) {
		String cqlquery = "select * from combatRoom where name=" + dungeonRoomName + ";";
		List<TaskStageMonster> list = getCombatRoomEntityManager().get().find(cqlquery);
		return list.get(0);
	}
	
	public List<TaskStageMonster> getCombatRoomUnlockedAtLevel(int level) {
		String cqlquery = "select * from combatRoom where lvl_required=" + level + ";";
		List<TaskStageMonster> list = getCombatRoomEntityManager().get().find(cqlquery);
		return list;
	}

	
	public  void reload() {
		setStaticIdsToCombatRooms();
	}
	

	public TaskStageMonsterEntityManager getCombatRoomEntityManager() {
		return taskStageMonsterEntityManager;
	}

	public void setCombatRoomEntityManager(
			TaskStageMonsterEntityManager taskStageMonsterEntityManager) {
		this.taskStageMonsterEntityManager = taskStageMonsterEntityManager;
	}
}
