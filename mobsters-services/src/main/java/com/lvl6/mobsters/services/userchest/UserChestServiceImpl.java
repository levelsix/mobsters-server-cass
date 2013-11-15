package com.lvl6.mobsters.services.userchest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.ConsumableEntityManager;
import com.lvl6.mobsters.entitymanager.UserChestEntityManager;
import com.lvl6.mobsters.entitymanager.UserConsumableEntityManager;
import com.lvl6.mobsters.po.UserChest;

@Component
public class UserChestServiceImpl implements UserChestService {
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, UserChest> idsToUserChests;
	
	@Autowired
	protected UserConsumableEntityManager userConsumableEntityManager;
	
	@Autowired
	protected ConsumableEntityManager consumableEntityManager;
	
	@Autowired
	protected UserChestEntityManager userChestEntityManager;

	@Override
	public  UserChest getUserChestForId(UUID id) {
		log.debug("retrieve userChest data for id " + id);
		if (idsToUserChests == null) {
			setStaticIdsToUserChests();      
		}
		return idsToUserChests.get(id);
	}
	
	@Override
	public  Map<UUID, UserChest> getUserChestsForIds(List<UUID> ids) {
		log.debug("retrieve userChests data for ids " + ids);
		if (idsToUserChests == null) {
			setStaticIdsToUserChests();      
		}
		Map<UUID, UserChest> toreturn = new HashMap<UUID, UserChest>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToUserChests.get(id));
		}
		return toreturn;
	}

	private  void setStaticIdsToUserChests() {
		log.debug("setting  map of userChestIds to userChests");

		String cqlquery = "select * from userChest;"; 
		List <UserChest> list = getUserChestEntityManager().get().find(cqlquery);
		idsToUserChests = new HashMap<UUID, UserChest>();
		for(UserChest c : list) {
			UUID id= c.getId();
			idsToUserChests.put(id, c);
		}
	}

	@Override
	public  List<UserChest> getAllUserChestsForUser(UUID userId) {
		String cqlquery = "select * from user_chest where user_id=" + userId + ";"; 
		List <UserChest> list = getUserChestEntityManager().get().find(cqlquery);
		return list;
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

	public UserChestEntityManager getUserChestEntityManager() {
		return userChestEntityManager;
	}

	public void setUserChestEntityManager(
			UserChestEntityManager userChestEntityManager) {
		this.userChestEntityManager = userChestEntityManager;
	}


	
}