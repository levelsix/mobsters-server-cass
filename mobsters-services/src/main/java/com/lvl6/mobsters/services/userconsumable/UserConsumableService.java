package com.lvl6.mobsters.services.userconsumable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.QuestForUserEntityManager;
import com.lvl6.mobsters.noneventprotos.UserConsumable.UserConsumablesProto;
import com.lvl6.mobsters.po.Consumable;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;

public interface UserConsumableService {
		
	public abstract Map<QuestForUser, Integer> getConsumablesBeingMade(UUID userId);
	
	public abstract Map<QuestForUser, Integer> convertListToMap(List<UserConsumablesProto> ucpList);
	
	public abstract QuestForUser getUserConsumableCorrespondingToUserConsumableProto(String userConsumableString);
	
	public abstract Consumable getConsumableCorrespondingToUserConsumable(String consumableName);
		
	public abstract void deleteUserConsumables(Collection<UUID> ids);
	
	public abstract void saveUserConsumables(Collection<QuestForUser> newStuff);
		
	public abstract QuestForUserEntityManager getUserEquipmentRepairEntityManager();
	
	public abstract void setUserEquipmentRepairEntityManager(QuestForUserEntityManager questForUserEntityManager);
	
	public abstract void deleteFromQueue(Map<QuestForUser, Integer> ucqDeleteMap, Map<QuestForUser, Integer> currentQueue);

}