package com.lvl6.mobsters.services.userequiprepair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.EquipmentEntityManager;
import com.lvl6.mobsters.entitymanager.UserEquipRepairEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.EquipmentRetrieveUtils;
import com.lvl6.mobsters.noneventprotos.UserEquipRepair.UserEquipRepairProto;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.UserEquip;
import com.lvl6.mobsters.po.UserEquipRepair;

import com.netflix.astyanax.*;

@Component
public class UserEquipRepairServiceImpl implements UserEquipRepairService {
	
	@Autowired
	protected UserEquipRepairEntityManager userEquipRepairEntityManager;
		
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, UserEquipRepair> idsToUserEquipRepairs;
	
	
	@Autowired
	protected EquipmentEntityManager equipmentEntityManager;
	
	@Autowired
	protected EquipmentRetrieveUtils equipmentRetrieveUtils;
	
	
	@Override
	public Map<UUID, UserEquipRepair> getEquipsBeingRepaired(String userIdString) {
		Map<UUID, UserEquipRepair> returnVal = new HashMap<UUID, UserEquipRepair>();
		
		//all the equips that are in repair
		String cqlQuery = "select * " +
						  "from user_equip_repair " +
						  "where user_id = " + userIdString;
		List<UserEquipRepair> inDbMap = 
				getUserEquipmentRepairEntityManager().get().find(cqlQuery);
		
		for (UserEquipRepair uer : inDbMap) {
			UUID id = uer.getId();
			
			returnVal.put(id, uer);
		}
		return returnVal;
	}
	
	//multiplier: 1 or -1, ueList or uerList is defined other is null
	@Override
	public Map<Integer, Integer> calculateRepairCost(List<UserEquip> ueList,
			List<UserEquipRepair> uerList, int multiplier) {
		//TODO: IMPLEMENT THIS
		return new HashMap<Integer, Integer>();
	}
	
	@Override
	public int calculateSingleUserEquipRepairCost(UserEquip ue) {
		//TODO: implement
		return 1000000;
	}
	
	
	@Override
	public void deleteUserEquipRepairs(Collection<UUID> ids) {
		getUserEquipmentRepairEntityManager().get().delete(ids);
	}
	
	@Override
	public void saveUserEquipRepairs(Collection<UserEquipRepair> newStuff) {
		getUserEquipmentRepairEntityManager().get().put(newStuff);
	}
	
	//returns in seconds
	@Override
	public int calculateTotalTimeOfQueuedUserEquips(List<UserEquipRepairProto> queuedEquips, Date currentTime) {
		List<UserEquipRepair> queuedEquipsList = new ArrayList<>();
		for(UserEquipRepairProto queuedEquip : queuedEquips) {
			UserEquipRepair uer = new UserEquipRepair();
			uer.setDurability(queuedEquip.getDurability());
			Date d = new Date(queuedEquip.getExpectedStartMillis());
			uer.setExpectedStart(d);
			queuedEquipsList.add(uer);
		}
		
		Keyspace k;
		k.prepareCqlStatement()
		
		int secondsRemaining = 0;
		for(UserEquipRepair uer2 : queuedEquipsList) {
			double durabilityTimeConstant = getEquipmentCorrespondingToUserEquipRepair(uer2).getDurabilityFixTimeConstant();
			double amountDamaged = 100.0-uer2.getDurability();
			int secondsToRepair = (int)(durabilityTimeConstant * amountDamaged);
			if(uer2.getExpectedStart().getTime() < currentTime.getTime()) {
				int secondsRemainingOnCurrentEquip = secondsToRepair - (int)((currentTime.getTime() - uer2.getExpectedStart().getTime())/1000);
				secondsRemaining +=  secondsRemainingOnCurrentEquip;
			}
			else secondsRemaining += secondsToRepair;
		}
		
		return secondsRemaining;
	}

	@Override
	public UserEquipRepair getUserEquipRepairForId(UUID id)
	 {
		log.debug("retrieve UserEquipRepair data for id " + id);
		if (idsToUserEquipRepairs == null) {
			setStaticIdsToUserEquipRepairs();      
		}
		return idsToUserEquipRepairs.get(id);
	}

	@Override
	public  Map<UUID, UserEquipRepair> getUserEquipRepairsForIds(List<UUID> ids) {
		log.debug("retrieve UserEquipRepairs data for ids " + ids);
		if (idsToUserEquipRepairs == null) {
			setStaticIdsToUserEquipRepairs();      
		}
		Map<UUID, UserEquipRepair> toreturn = new HashMap<UUID, UserEquipRepair>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToUserEquipRepairs.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToUserEquipRepairs() {
		log.debug("setting  map of UserEquipRepairIds to UserEquipRepairs");
		
		String cqlquery = "select * from user_equip_repair;"; 
		List <UserEquipRepair> list = getUserEquipRepairEntityManager().get().find(cqlquery);
		idsToUserEquipRepairs = new HashMap<UUID, UserEquipRepair>();
		for(UserEquipRepair us : list) {
			UUID id= us.getId();
			idsToUserEquipRepairs.put(id, us);
		}
					
	}

	@Override
	public  List<UserEquipRepair> getAllUserEquipRepairsForUser(UUID userId) {
		String cqlquery = "select * from user_equip_repair where user_id=" + userId + ";"; 
		List <UserEquipRepair> list = getUserEquipRepairEntityManager().get().find(cqlquery);
		return list;
	}
	
	@Override
	public Equipment getEquipmentCorrespondingToUserEquipRepair(UserEquipRepair ue) {
		UUID equipId = ue.getEquipId();
		return getEquipmentRetrieveUtils().getEquipmentForId(equipId);
	}
	
	
	
	@Override
	public UserEquipRepairEntityManager getUserEquipmentRepairEntityManager() {
		return userEquipRepairEntityManager;
	}
	
	@Override
	public void setUserEquipmentRepairEntityManager(
			UserEquipRepairEntityManager userEquipRepairEntityManager) {
		this.userEquipRepairEntityManager = userEquipRepairEntityManager;
	}

	public UserEquipRepairEntityManager getUserEquipRepairEntityManager() {
		return userEquipRepairEntityManager;
	}

	public void setUserEquipRepairEntityManager(
			UserEquipRepairEntityManager userEquipRepairEntityManager) {
		this.userEquipRepairEntityManager = userEquipRepairEntityManager;
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


	
	
	
