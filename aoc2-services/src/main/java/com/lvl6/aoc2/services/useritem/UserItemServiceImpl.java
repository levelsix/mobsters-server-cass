package com.lvl6.aoc2.services.useritem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.ItemEntityManager;
import com.lvl6.aoc2.entitymanager.UserItemEntityManager;
import com.lvl6.aoc2.entitymanager.staticdata.ItemRetrieveUtils;
import com.lvl6.aoc2.po.Item;
import com.lvl6.aoc2.po.UserItem;


@Component
public class UserItemServiceImpl implements UserItemService {
	
	@Autowired
	protected UserItemEntityManager userItemEntityManager;
	
	@Autowired
	protected ItemEntityManager itemEntityManager;
	
	@Autowired
	protected ItemRetrieveUtils itemRetrieveUtils;
	
	
	private  Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private  Map<UUID, UserItem> idsToUserItems;

	
	@Override
	public Map<UUID, UserItem> getUserItemsByUserItemIds(Collection<UUID> ids) {
		Map<UUID, UserItem> returnVal = new HashMap<UUID, UserItem>();
		
		List<UserItem> ueList = userItemEntityManager.get().get(ids);
		for (UserItem ue : ueList) {
			UUID id = ue.getId();
			returnVal.put(id, ue);
		}
		
		return returnVal;
	}
	
	@Override
	public void saveItems(Collection<UserItem> newItems) {
		getUserItemEntityManager().get().put(newItems);
	}

	@Override
	public int getNumberOfSpecificUserKeys(UUID keyId, UUID userId) {
		List<UserItem> uiList = getAllUserItemsForUser(userId);
		int specificKeyCount = 0;
		for(UserItem ui : uiList) {
			if(ui.getItemId() == keyId) {
				specificKeyCount++;
			}
		}
		return specificKeyCount;
	}

	@Override
	public  UserItem getUserItemForId(UUID id) {
		log.debug("retrieve UserItem data for id " + id);
		if (idsToUserItems == null) {
			setStaticIdsToUserItems();      
		}
		return idsToUserItems.get(id);
	}

	@Override
	public  Map<UUID, UserItem> getUserItemsForIds(List<UUID> ids) {
		log.debug("retrieve UserItems data for ids " + ids);
		if (idsToUserItems == null) {
			setStaticIdsToUserItems();      
		}
		Map<UUID, UserItem> toreturn = new HashMap<UUID, UserItem>();
		for (UUID id : ids) {
			toreturn.put(id,  idsToUserItems.get(id));
		}
		return toreturn;
	}

	
	private  void setStaticIdsToUserItems() {
		log.debug("setting  map of UserItemIds to UserItems");

		String cqlquery = "select * from user_item;"; 
		List <UserItem> list = getUserItemEntityManager().get().find(cqlquery);
		idsToUserItems = new HashMap<UUID, UserItem>();
		for(UserItem us : list) {
			UUID id= us.getId();
			idsToUserItems.put(id, us);
		}
					
	}

	@Override
	public  List<UserItem> getAllUserItemsForUser(UUID userId) {
		String cqlquery = "select * from user_item where user_id=" + userId + ";"; 
		List <UserItem> list = getUserItemEntityManager().get().find(cqlquery);
		return list;
	}
	
	@Override
	public Item getItemCorrespondingToUserItem(UserItem ui) {
		return getItemRetrieveUtils().getItemAccordingToName(ui.getItemId());
	}
	
	
	
	@Override
	public UserItemEntityManager getUserItemEntityManager() {
		return userItemEntityManager;
	}

	@Override
	public void setUserItemEntityManager(
			UserItemEntityManager userItemEntityManager) {
		this.userItemEntityManager = userItemEntityManager;
	}


	public ItemEntityManager getItemEntityManager() {
		return itemEntityManager;
	}

	public void setItemEntityManager(ItemEntityManager itemEntityManager) {
		this.itemEntityManager = itemEntityManager;
	}

	public ItemRetrieveUtils getItemRetrieveUtils() {
		return itemRetrieveUtils;
	}

	public void setItemRetrieveUtils(ItemRetrieveUtils itemRetrieveUtils) {
		this.itemRetrieveUtils = itemRetrieveUtils;
	}
	
	
	
}