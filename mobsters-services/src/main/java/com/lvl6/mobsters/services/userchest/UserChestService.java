package com.lvl6.mobsters.services.userchest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.po.UserChest;

public interface UserChestService {
		
	public abstract UserChest getUserChestForId(UUID id);
	
	public abstract Map<UUID, UserChest> getUserChestsForIds(List<UUID> ids);
	
	public abstract List<UserChest> getAllUserChestsForUser(UUID userId);
	
	
	
}