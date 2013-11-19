package com.lvl6.mobsters.services.userconsumable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.ConsumableEntityManager;
import com.lvl6.mobsters.entitymanager.nonstaticdata.QuestForUserEntityManager;
import com.lvl6.mobsters.noneventprotos.UserConsumable.UserConsumablesProto;
import com.lvl6.mobsters.po.Consumable;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;

@Component
public class UserConsumableServiceImpl implements UserConsumableService {
	
	@Autowired
	protected QuestForUserEntityManager questForUserEntityManager;
	
	@Autowired
	protected ConsumableEntityManager consumableEntityManager;

		
	
	@Override
	public Map<QuestForUser, Integer> getConsumablesBeingMade(UUID userId) {
		Map<QuestForUser, Integer> returnVal = new HashMap<QuestForUser, Integer>();
		
		//all the consumables that are in queue
		String cqlQuery = "select * " +
						  "from user_consumable_queue " +
						  "where user_id = " + userId;
		List<QuestForUser> inDbMap = 
				getUserEquipmentRepairEntityManager().get().find(cqlQuery);
		
		for (QuestForUser uer : inDbMap) {
			Integer quantity = uer.getQuantity();
			
			returnVal.put(uer, quantity);
		}
		return returnVal;
	}
	
	//TODO: CHANGE ARGUMENT TO ONLY ACCEPT List<String>
	@Override
	public Map<QuestForUser, Integer> convertListToMap(List<UserConsumablesProto> ucpList) {
		Map <QuestForUser, Integer> returnMap = new HashMap<>();
		for(UserConsumablesProto ucp : ucpList) {
			String userConsumableName = "";//ucp.getName();
			QuestForUser uc = getUserConsumableCorrespondingToUserConsumableProto(userConsumableName);
					
			if(returnMap.containsKey(uc)) {
				int quantity = returnMap.get(uc);
				returnMap.put(uc, quantity+1);
			}
			else {
				returnMap.put(uc, 1);
			}
		}
		return returnMap;
	}
	
	@Override
	public QuestForUser getUserConsumableCorrespondingToUserConsumableProto(String userConsumableName) {
		String cqlquery = "select * from user_consumable where name= " + userConsumableName + ";";
		List<QuestForUser> uc = getUserConsumableEntityManager().get().find(cqlquery);
		return uc.get(0);
	}
	
	@Override
	public Consumable getConsumableCorrespondingToUserConsumable(String consumableName) {
		String cqlquery = "select * from consumable where name= " + consumableName + ";";
		List<Consumable> c = getConsumableEntityManager().get().find(cqlquery);
		return c.get(0);
	}
	
	
	@Override
	public void deleteUserConsumables(Collection<UUID> ids) {
		getUserEquipmentRepairEntityManager().get().delete(ids);
	}
	
	@Override
	public void saveUserConsumables(Collection<QuestForUser> newStuff) {
		getUserEquipmentRepairEntityManager().get().put(newStuff);
	}
	
	@Override
	public void deleteFromQueue(Map<QuestForUser, Integer> ucqDeleteMap, Map<QuestForUser, Integer> currentQueue) {
		for(Map.Entry<QuestForUser, Integer> entry : ucqDeleteMap.entrySet()) {
			QuestForUser consumableRemoved = entry.getKey();
			Integer quantityRemoved = entry.getValue();
			for(Map.Entry<QuestForUser, Integer> entry2 : currentQueue.entrySet()) {
				QuestForUser consumableInQueue = entry2.getKey();
				if(consumableRemoved.getConsumableId() == consumableInQueue.getConsumableId()) {
					if(consumableInQueue.getQuantity() == quantityRemoved) {
						getUserConsumableEntityManager().get().delete(consumableInQueue.getId());
					}
					else {
						consumableInQueue.setQuantity(consumableInQueue.getQuantity() - quantityRemoved);
						getUserConsumableEntityManager().get().put(consumableInQueue);
					}
				}
			}
		}
		
	}
	

	@Override
	public QuestForUserEntityManager getUserEquipmentRepairEntityManager() {
		return questForUserEntityManager;
	}
	
	@Override
	public void setUserEquipmentRepairEntityManager(
			QuestForUserEntityManager questForUserEntityManager) {
		this.questForUserEntityManager = questForUserEntityManager;
	}

	public QuestForUserEntityManager getUserConsumableEntityManager() {
		return questForUserEntityManager;
	}

	public void setUserConsumableEntityManager(
			QuestForUserEntityManager questForUserEntityManager) {
		this.questForUserEntityManager = questForUserEntityManager;
	}

	public ConsumableEntityManager getConsumableEntityManager() {
		return consumableEntityManager;
	}

	public void setConsumableEntityManager(
			ConsumableEntityManager consumableEntityManager) {
		this.consumableEntityManager = consumableEntityManager;
	}


	
}