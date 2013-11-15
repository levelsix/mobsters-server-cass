package com.lvl6.aoc2.services.useritem;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.aoc2.entitymanager.UserItemEntityManager;
import com.lvl6.aoc2.po.Item;
import com.lvl6.aoc2.po.UserItem;

public interface UserItemService {
	
	public abstract Map<UUID, UserItem> getUserItemsByUserItemIds (Collection<UUID> ids);
	
	public abstract void saveItems(Collection<UserItem> newItems);
	
	public abstract int getNumberOfSpecificUserKeys(UUID keyId, UUID userId);
	
	public abstract UserItemEntityManager getUserItemEntityManager();
	
	public abstract void setUserItemEntityManager(UserItemEntityManager userItemEntityManager);
	
	public abstract UserItem getUserItemForId(UUID id);
	
	public abstract Map<UUID, UserItem> getUserItemsForIds(List<UUID> ids);
	
	public abstract List<UserItem> getAllUserItemsForUser(UUID userId);
	
	public abstract Item getItemCorrespondingToUserItem(UserItem ui);
	
}