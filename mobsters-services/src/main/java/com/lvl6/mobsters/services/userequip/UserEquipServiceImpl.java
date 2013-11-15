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
import com.lvl6.mobsters.entitymanager.UserEquipEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.EquipmentRetrieveUtils;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.UserEquip;


@Component
public class UserEquipServiceImpl implements UserEquipService {
	
	@Autowired
	protected UserEquipEntityManager userEquipEntityManager;
	
	@Autowired
	protected EquipmentRetrieveUtils equipmentRetrieveUtils;
	
	@Autowired
	protected EquipmentEntityManager equipmentEntityManager;

	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, UserEquip> idsToUserEquips;
	
	@Override
	public Map<UUID, UserEquip> getUserEquipsByUserEquipIds(Collection<UUID> ids) {
		Map<UUID, UserEquip> returnVal = new HashMap<UUID, UserEquip>();
		
		List<UserEquip> ueList = userEquipEntityManager.get().get(ids);
		for (UserEquip ue : ueList) {
			UUID id = ue.getId();
			returnVal.put(id, ue);
		}
		
		return returnVal;
	}
	
	@Override
	public void saveEquips(Collection<UserEquip> newEquips) {
		getUserEquipEntityManager().get().put(newEquips);
	}
	
	@Override
	public void getEquippedUserEquips(List<UserEquip> allUserEquips, List<UserEquip> equippedUserEquips) {
		for(UserEquip ue : allUserEquips) {
			if(ue.isEquipped())
				equippedUserEquips.add(ue);
		}
	}
	
	public  UserEquip getUserEquipForId(UUID id) {
		log.debug("retrieve UserEquip data for id " + id);
		if (idsToUserEquips == null) {
			setStaticIdsToUserEquips();      
		}
		return idsToUserEquips.get(id);
	}

	public  Map<UUID, UserEquip> getUserEquipsForIds(List<UUID> ids) {
		log.debug("retrieve UserEquips data for ids " + ids);
		if (idsToUserEquips == null) {
			setStaticIdsToUserEquips();      
		}
		Map<UUID, UserEquip> toreturn = new HashMap<UUID, UserEquip>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToUserEquips.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToUserEquips() {
		log.debug("setting  map of UserEquipIds to UserEquips");

		String cqlquery = "select * from user_equip;"; 
		List <UserEquip> list = getUserEquipEntityManager().get().find(cqlquery);
		idsToUserEquips = new HashMap<UUID, UserEquip>();
		for(UserEquip us : list) {
			UUID id= us.getId();
			idsToUserEquips.put(id, us);
		}
					
	}

	public  List<UserEquip> getAllUserEquipsForUser(UUID userId) {
		String cqlquery = "select * from user_equip where user_id=" + userId + ";"; 
		List <UserEquip> list = getUserEquipEntityManager().get().find(cqlquery);
		return list;
	}
	
	public Equipment getEquipmentCorrespondingToUserEquip(UserEquip ue) {
		UUID equipId = ue.getEquipId();
		return getEquipmentRetrieveUtils().getEquipmentForId(equipId);
	}
	
	public List<UserEquip> getAllEquippedUserEquipsForUser(UUID userId) {
		List<UserEquip> ueList = getAllUserEquipsForUser(userId);
		List<UserEquip> equippedList = new ArrayList<>();
		for(UserEquip ue : ueList) {
			if(ue.isEquipped()) {
				equippedList.add(ue);
			}
		}
		return equippedList;
	}
	
	
	@Override
	public UserEquipEntityManager getUserEquipEntityManager() {
		return userEquipEntityManager;
	}

	@Override
	public void setUserEquipEntityManager(
			UserEquipEntityManager userEquipEntityManager) {
		this.userEquipEntityManager = userEquipEntityManager;
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