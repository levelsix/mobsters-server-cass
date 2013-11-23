//package com.lvl6.mobsters.services.userconsumablequeue;
//
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.mobsters.entitymanager.ConsumableEntityManager;
//import com.lvl6.mobsters.entitymanager.UserConsumableQueueEntityManager;
//import com.lvl6.mobsters.noneventprotos.UserConsumableQueue.UserConsumableQueueProto;
//import com.lvl6.mobsters.po.Consumable;
//import com.lvl6.mobsters.po.UserConsumableQueue;
//
//@Component
//public class UserConsumableQueueServiceImpl implements UserConsumableQueueService {
//	
//	@Autowired
//	protected UserConsumableQueueEntityManager userConsumableQueueEntityManager;
//	
//	@Autowired
//	protected ConsumableEntityManager consumableEntityManager;
//
//		
//	
//	@Override
//	public Map<UserConsumableQueue, Integer> getConsumablesBeingMade(UUID userId) {
//		Map<UserConsumableQueue, Integer> returnVal = new HashMap<UserConsumableQueue, Integer>();
//		
//		//all the consumables that are in queue
//		String cqlQuery = "select * " +
//						  "from user_consumable_queue " +
//						  "where user_id = " + userId;
//		List<UserConsumableQueue> inDbMap = 
//				getUserEquipmentRepairEntityManager().get().find(cqlQuery);
//		
//		for (UserConsumableQueue uer : inDbMap) {
//			Integer quantity = uer.getQuantity();
//			
//			returnVal.put(uer, quantity);
//		}
//		return returnVal;
//	}
//	
//	@Override
//	public Map<UserConsumableQueue, Integer> convertListToMap(List<UserConsumableQueueProto> ucqpList) {
//		Map <UserConsumableQueue, Integer> returnMap = new HashMap<>();
//		for(UserConsumableQueueProto ucqp : ucqpList) {
//			String userConsumableQueueString = ucqp.getUserConsumableQueueID();
//			UserConsumableQueue ucq = getUserConsumableQueueCorrespondingToUserConsumableQueueProto(userConsumableQueueString);
//					
//			if(returnMap.containsKey(ucq)) {
//				int quantity = returnMap.get(ucq);
//				returnMap.put(ucq, quantity+1);
//			}
//			else {
//				returnMap.put(ucq, 1);
//			}
//		}
//		return returnMap;
//	}
//	
//	@Override
//	public UserConsumableQueue getUserConsumableQueueCorrespondingToUserConsumableQueueProto(String userConsumableQueueString) {
//		UUID userConsumableQueueID = UUID.fromString(userConsumableQueueString);
//		String cqlquery = "select * from user_consumable_queue where id= " + userConsumableQueueID + ";";
//		List<UserConsumableQueue> ucq = getUserConsumableQueueEntityManager().get().find(cqlquery);
//		return ucq.get(0);
//	}
//	
//	@Override
//	public Consumable getConsumableCorrespondingToUserConsumableQueue(String consumableId) {
//		String cqlquery = "select * from consumable where id= " + consumableId + ";";
//		List<Consumable> c = getConsumableEntityManager().get().find(cqlquery);
//		return c.get(0);
//	}
//	
//	@Override
//	public int calculateBuildCost(Map<UserConsumableQueue, Integer> ucqMap) {
//		int totalBuildCost = 0;
//		for(Map.Entry<UserConsumableQueue, Integer> entry : ucqMap.entrySet()) {
//			UserConsumableQueue ucq = entry.getKey();
//			Integer quantity = entry.getValue();
//			String consumableId = ucq.getConsumableId().toString();
//			totalBuildCost += getConsumableCorrespondingToUserConsumableQueue(consumableId).getCost()*quantity;
//		}
//		
//		return totalBuildCost;
//	}
//	
//	
//	@Override
//	public void deleteUserConsumableQueues(Collection<UUID> ids) {
//		getUserEquipmentRepairEntityManager().get().delete(ids);
//	}
//	
//	@Override
//	public void saveUserConsumableQueues(Collection<UserConsumableQueue> newStuff) {
//		getUserEquipmentRepairEntityManager().get().put(newStuff);
//	}
//	
//	//returns in seconds
//	@Override
//	public int calculateTotalTimeOfQueuedUserConsumable(Map<UserConsumableQueue, Integer> queuedConsumables, Date currentTime) {	
//		int totalBuildTime = 0;
//		for(Map.Entry<UserConsumableQueue, Integer> entry : queuedConsumables.entrySet()) {
//			UserConsumableQueue ucq = entry.getKey();
//			Integer quantity = entry.getValue();
//			String consumableId = ucq.getConsumableId().toString();
//			totalBuildTime += getConsumableCorrespondingToUserConsumableQueue(consumableId).getCreateTimeSeconds()*quantity;
//		}
//		
//		return totalBuildTime;
//	}
//
//	public void deleteFromQueue(Map<UserConsumableQueue, Integer> ucqDeleteMap, Map<UserConsumableQueue, Integer> currentQueue) {
//		for(Map.Entry<UserConsumableQueue, Integer> entry : ucqDeleteMap.entrySet()) {
//			UserConsumableQueue consumableRemoved = entry.getKey();
//			Integer quantityRemoved = entry.getValue();
//			for(Map.Entry<UserConsumableQueue, Integer> entry2 : currentQueue.entrySet()) {
//				UserConsumableQueue consumableInQueue = entry2.getKey();
//				if(consumableRemoved.getConsumableId() == consumableInQueue.getConsumableId()) {
//					if(consumableInQueue.getQuantity() == quantityRemoved) {
//						getUserConsumableQueueEntityManager().get().delete(consumableInQueue.getId());
//					}
//					else {
//						consumableInQueue.setQuantity(consumableInQueue.getQuantity() - quantityRemoved);
//						getUserConsumableQueueEntityManager().get().put(consumableInQueue);
//					}
//				}
//			}
//		}
//	}
//	
//
//	@Override
//	public UserConsumableQueueEntityManager getUserEquipmentRepairEntityManager() {
//		return userConsumableQueueEntityManager;
//	}
//	
//	@Override
//	public void setUserEquipmentRepairEntityManager(
//			UserConsumableQueueEntityManager userConsumableQueueEntityManager) {
//		this.userConsumableQueueEntityManager = userConsumableQueueEntityManager;
//	}
//
//	public UserConsumableQueueEntityManager getUserConsumableQueueEntityManager() {
//		return userConsumableQueueEntityManager;
//	}
//
//	public void setUserConsumableQueueEntityManager(
//			UserConsumableQueueEntityManager userConsumableQueueEntityManager) {
//		this.userConsumableQueueEntityManager = userConsumableQueueEntityManager;
//	}
//
//	public ConsumableEntityManager getConsumableEntityManager() {
//		return consumableEntityManager;
//	}
//
//	public void setConsumableEntityManager(
//			ConsumableEntityManager consumableEntityManager) {
//		this.consumableEntityManager = consumableEntityManager;
//	}
//
//
//	
//}