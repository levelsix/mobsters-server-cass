package com.lvl6.mobsters.services.userequiprepair;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.UserEquipRepairEntityManager;
import com.lvl6.mobsters.noneventprotos.UserEquipRepair.UserEquipRepairProto;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.UserEquip;
import com.lvl6.mobsters.po.UserEquipRepair;

public interface UserEquipRepairService {
	
	public abstract Map<UUID, UserEquipRepair> getEquipsBeingRepaired(String userIdString);
	
	//returns AocTwoTableConstants.resourceType -> amount
	public abstract Map<Integer, Integer> calculateRepairCost(List<UserEquip> ueList,
			List<UserEquipRepair> uerList, int multiplier);
	
	public abstract void deleteUserEquipRepairs(Collection<UUID> ids);
	
	public abstract void saveUserEquipRepairs(Collection<UserEquipRepair> newStuff);
	
	public abstract int calculateSingleUserEquipRepairCost(UserEquip ue);

	public abstract int calculateTotalTimeOfQueuedUserEquips(List<UserEquipRepairProto> queuedEquips, Date currentTime);
	
	public abstract UserEquipRepairEntityManager getUserEquipmentRepairEntityManager();
	
	public abstract void setUserEquipmentRepairEntityManager(UserEquipRepairEntityManager userEquipRepairEntityManager);

	public abstract UserEquipRepair getUserEquipRepairForId(UUID id);
	
	public abstract Map<UUID, UserEquipRepair> getUserEquipRepairsForIds(List<UUID> ids);
	
	public abstract List<UserEquipRepair> getAllUserEquipRepairsForUser(UUID userId);
	
	public abstract Equipment getEquipmentCorrespondingToUserEquipRepair(UserEquipRepair ue);

}