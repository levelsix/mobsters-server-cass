package com.lvl6.mobsters.services.userconsumable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.ConsumableEntityManager;
import com.lvl6.mobsters.entitymanager.UserConsumableEntityManager;
import com.lvl6.mobsters.noneventprotos.UserConsumable.UserConsumablesProto;
import com.lvl6.mobsters.po.Consumable;
import com.lvl6.mobsters.po.UserConsumable;

@Component
public class UserConsumableServiceImpl implements UserConsumableService {
	
	@Autowired
	protected UserConsumableEntityManager userConsumableEntityManager;
	
	@Autowired
	protected ConsumableEntityManager consumableEntityManager;

		
	
	@Override
	public Map<UserConsumable, Integer> getConsumablesBeingMade(UUID userId) {
		Map<UserConsumable, Integer> returnVal = new HashMap<UserConsumable, Integer>();
		
		//all the consumables that are in queue
		String cqlQuery = "select * " +
						  "from user_consumable_queue " +
						  "where user_id = " + userId;
		List<UserConsumable> inDbMap = 
				getUserEquipmentRepairEntityManager().get().find(cqlQuery);
		
		for (UserConsumable uer : inDbMap) {
			Integer quantity = uer.getQuantity();
			
			returnVal.put(uer, quantity);
		}
		return returnVal;
	}
	
	//TODO: CHANGE ARGUMENT TO ONLY ACCEPT List<String>
	@Override
	public Map<UserConsumable, Integer> convertListToMap(List<UserConsumablesProto> ucpList) {
		Map <UserConsumable, Integer> returnMap = new HashMap<>();
		for(UserConsumablesProto ucp : ucpList) {
			String userConsumableName = "";//ucp.getName();
			UserConsumable uc = getUserConsumableCorrespondingToUserConsumableProto(userConsumableName);
					
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
	public UserConsumable getUserConsumableCorrespondingToUserConsumableProto(String userConsumableName) {
		String cqlquery = "select * from user_consumable where name= " + userConsumableName + ";";
		List<UserConsumable> uc = getUserConsumableEntityManager().get().find(cqlquery);
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
	public void saveUserConsumables(Collection<UserConsumable> newStuff) {
		getUserEquipmentRepairEntityManager().get().put(newStuff);
	}
	
	@Override
	public void deleteFromQueue(Map<UserConsumable, Integer> ucqDeleteMap, Map<UserConsumable, Integer> currentQueue) {
		for(Map.Entry<UserConsumable, Integer> entry : ucqDeleteMap.entrySet()) {
			UserConsumable consumableRemoved = entry.getKey();
			Integer quantityRemoved = entry.getValue();
			for(Map.Entry<UserConsumable, Integer> entry2 : currentQueue.entrySet()) {
				UserConsumable consumableInQueue = entry2.getKey();
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
	public UserConsumableEntityManager getUserEquipmentRepairEntityManager() {
		return userConsumableEntityManager;
	}
	
	@Override
	public void setUserEquipmentRepairEntityManager(
			UserConsumableEntityManager userConsumableEntityManager) {
		this.userConsumableEntityManager = userConsumableEntityManager;
	}

	public UserConsumableEntityManager getUserConsumableEntityManager() {
		return userConsumableEntityManager;
	}

	public void setUserConsumableEntityManager(
			UserConsumableEntityManager userConsumableEntityManager) {
		this.userConsumableEntityManager = userConsumableEntityManager;
	}

	public ConsumableEntityManager getConsumableEntityManager() {
		return consumableEntityManager;
	}

	public void setConsumableEntityManager(
			ConsumableEntityManager consumableEntityManager) {
		this.consumableEntityManager = consumableEntityManager;
	}


	
}