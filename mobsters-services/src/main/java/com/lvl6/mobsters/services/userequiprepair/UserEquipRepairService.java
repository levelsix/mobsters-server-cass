package com.lvl6.mobsters.services.userequiprepair;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.MonsterHealingForUserEntityManager;
import com.lvl6.mobsters.noneventprotos.UserEquipRepair.UserEquipRepairProto;
import com.lvl6.mobsters.po.Equipment;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;

public interface UserEquipRepairService {
	
	public abstract Map<UUID, MonsterHealingForUser> getEquipsBeingRepaired(String userIdString);
	
	//returns MobstersTableConstants.resourceType -> amount
	public abstract Map<Integer, Integer> calculateRepairCost(List<MonsterForUser> ueList,
			List<MonsterHealingForUser> uerList, int multiplier);
	
	public abstract void deleteUserEquipRepairs(Collection<UUID> ids);
	
	public abstract void saveUserEquipRepairs(Collection<MonsterHealingForUser> newStuff);
	
	public abstract int calculateSingleUserEquipRepairCost(MonsterForUser ue);

	public abstract int calculateTotalTimeOfQueuedUserEquips(List<UserEquipRepairProto> queuedEquips, Date currentTime);
	
	public abstract MonsterHealingForUserEntityManager getUserEquipmentRepairEntityManager();
	
	public abstract void setUserEquipmentRepairEntityManager(MonsterHealingForUserEntityManager monsterHealingForUserEntityManager);

	public abstract MonsterHealingForUser getUserEquipRepairForId(UUID id);
	
	public abstract Map<UUID, MonsterHealingForUser> getUserEquipRepairsForIds(List<UUID> ids);
	
	public abstract List<MonsterHealingForUser> getAllUserEquipRepairsForUser(UUID userId);
	
	public abstract Equipment getEquipmentCorrespondingToUserEquipRepair(MonsterHealingForUser ue);

}