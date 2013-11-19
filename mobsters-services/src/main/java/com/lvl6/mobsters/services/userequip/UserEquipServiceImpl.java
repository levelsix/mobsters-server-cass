package com.lvl6.mobsters.services.userequip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.EquipmentEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.EquipmentRetrieveUtils;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;


@Component
public class UserEquipServiceImpl implements UserEquipService {
	
	@Autowired
	protected MonsterForUserEntityManager monsterForUserEntityManager;
	
	@Autowired
	protected EquipmentRetrieveUtils equipmentRetrieveUtils;
	
	@Autowired
	protected EquipmentEntityManager equipmentEntityManager;

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, MonsterForUser> idsToUserEquips;
	
	@Override
	public Map<UUID, MonsterForUser> getUserEquipsByUserEquipIds(Collection<UUID> ids) {
		Map<UUID, MonsterForUser> returnVal = new HashMap<UUID, MonsterForUser>();
		
		List<MonsterForUser> ueList = monsterForUserEntityManager.get().get(ids);
		for (MonsterForUser ue : ueList) {
			UUID id = ue.getId();
			returnVal.put(id, ue);
		}
		
		return returnVal;
	}
	
	@Override
	public void saveEquips(Collection<MonsterForUser> newEquips) {
		getUserEquipEntityManager().get().put(newEquips);
	}
	
	@Override
	public void getEquippedUserEquips(List<MonsterForUser> allUserEquips, List<MonsterForUser> equippedUserEquips) {
		for(MonsterForUser ue : allUserEquips) {
			if(ue.isEquipped())
				equippedUserEquips.add(ue);
		}
	}
	
	public  MonsterForUser getUserEquipForId(UUID id) {
		log.debug("retrieve MonsterForUser data for id " + id);
		if (idsToUserEquips == null) {
			setStaticIdsToUserEquips();      
		}
		return idsToUserEquips.get(id);
	}

	public  Map<UUID, MonsterForUser> getUserEquipsForIds(List<UUID> ids) {
		log.debug("retrieve UserEquips data for ids " + ids);
		if (idsToUserEquips == null) {
			setStaticIdsToUserEquips();      
		}
		Map<UUID, MonsterForUser> toreturn = new HashMap<UUID, MonsterForUser>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToUserEquips.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToUserEquips() {
		log.debug("setting  map of UserEquipIds to UserEquips");

		String cqlquery = "select * from user_equip;"; 
		List <MonsterForUser> list = getUserEquipEntityManager().get().find(cqlquery);
		idsToUserEquips = new HashMap<UUID, MonsterForUser>();
		for(MonsterForUser us : list) {
			UUID id= us.getId();
			idsToUserEquips.put(id, us);
		}
					
	}

	public  List<MonsterForUser> getAllUserEquipsForUser(UUID userId) {
		String cqlquery = "select * from user_equip where user_id=" + userId + ";"; 
		List <MonsterForUser> list = getUserEquipEntityManager().get().find(cqlquery);
		return list;
	}
	
	public Equipment getEquipmentCorrespondingToUserEquip(MonsterForUser ue) {
		UUID equipId = ue.getEquipId();
		return getEquipmentRetrieveUtils().getEquipmentForId(equipId);
	}
	
	public List<MonsterForUser> getAllEquippedUserEquipsForUser(UUID userId) {
		List<MonsterForUser> ueList = getAllUserEquipsForUser(userId);
		List<MonsterForUser> equippedList = new ArrayList<>();
		for(MonsterForUser ue : ueList) {
			if(ue.isEquipped()) {
				equippedList.add(ue);
			}
		}
		return equippedList;
	}
	
	
	@Override
	public MonsterForUserEntityManager getUserEquipEntityManager() {
		return monsterForUserEntityManager;
	}

	@Override
	public void setUserEquipEntityManager(
			MonsterForUserEntityManager monsterForUserEntityManager) {
		this.monsterForUserEntityManager = monsterForUserEntityManager;
	}

	public EquipmentEntityManager getEquipmentEntityManager() {
		return equipmentEntityManager;
	}

	public void setEquipmentEntityManager(
			EquipmentEntityManager equipmentEntityManager) {
		this.equipmentEntityManager = equipmentEntityManager;
	}

	public EquipmentRetrieveUtils getEquipmentRetrieveUtils() {
		return equipmentRetrieveUtils;
	}

	public void setEquipmentRetrieveUtils(
			EquipmentRetrieveUtils equipmentRetrieveUtils) {
		this.equipmentRetrieveUtils = equipmentRetrieveUtils;
	}
	
	
	
	
}