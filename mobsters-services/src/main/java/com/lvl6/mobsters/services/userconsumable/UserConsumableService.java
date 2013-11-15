package com.lvl6.mobsters.services.userconsumable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.UserConsumableEntityManager;
import com.lvl6.mobsters.noneventprotos.UserConsumable.UserConsumablesProto;
import com.lvl6.mobsters.po.Consumable;
import com.lvl6.mobsters.po.UserConsumable;

public interface UserConsumableService {
		
	public abstract Map<UserConsumable, Integer> getConsumablesBeingMade(UUID userId);
	
	public abstract Map<UserConsumable, Integer> convertListToMap(List<UserConsumablesProto> ucpList);
	
	public abstract UserConsumable getUserConsumableCorrespondingToUserConsumableProto(String userConsumableString);
	
	public abstract Consumable getConsumableCorrespondingToUserConsumable(String consumableName);
		
	public abstract void deleteUserConsumables(Collection<UUID> ids);
	
	public abstract void saveUserConsumables(Collection<UserConsumable> newStuff);
		
	public abstract UserConsumableEntityManager getUserEquipmentRepairEntityManager();
	
	public abstract void setUserEquipmentRepairEntityManager(UserConsumableEntityManager userConsumableEntityManager);
	
	public abstract void deleteFromQueue(Map<UserConsumable, Integer> ucqDeleteMap, Map<UserConsumable, Integer> currentQueue);

}