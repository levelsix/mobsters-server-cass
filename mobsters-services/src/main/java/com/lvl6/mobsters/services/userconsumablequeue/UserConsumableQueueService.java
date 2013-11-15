package com.lvl6.mobsters.services.userconsumablequeue;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.UserConsumableQueueEntityManager;
import com.lvl6.mobsters.noneventprotos.UserConsumableQueue.UserConsumableQueueProto;
import com.lvl6.mobsters.po.Consumable;
import com.lvl6.mobsters.po.UserConsumableQueue;

public interface UserConsumableQueueService {
		
	public abstract Map<UserConsumableQueue, Integer> getConsumablesBeingMade(UUID userId);
	
	public abstract Map<UserConsumableQueue, Integer> convertListToMap(List<UserConsumableQueueProto> ucqpList);
	
	public abstract UserConsumableQueue getUserConsumableQueueCorrespondingToUserConsumableQueueProto(String userConsumableQueueString);
	
	public abstract Consumable getConsumableCorrespondingToUserConsumableQueue(String consumableName);
	
	public abstract int calculateBuildCost(Map<UserConsumableQueue, Integer> ucqMap);
	
	public abstract void deleteUserConsumableQueues(Collection<UUID> ids);
	
	public abstract void saveUserConsumableQueues(Collection<UserConsumableQueue> newStuff);
	
	public abstract int calculateTotalTimeOfQueuedUserConsumable(Map<UserConsumableQueue, Integer> queuedConsumables, Date currentTime);
	
	public abstract UserConsumableQueueEntityManager getUserEquipmentRepairEntityManager();
	
	public abstract void setUserEquipmentRepairEntityManager(UserConsumableQueueEntityManager userConsumableQueueEntityManager);
	
	public abstract void deleteFromQueue(Map<UserConsumableQueue, Integer> ucqDeleteMap, Map<UserConsumableQueue, Integer> currentQueue);

}