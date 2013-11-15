package com.lvl6.mobsters.services.userdungeonstatushistory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvl6.mobsters.po.UserDungeonStatusHistory;


public interface UserDungeonStatusHistoryService {
		
	public abstract UserDungeonStatusHistory getUserDungeonStatusHistoryForId(UUID id);
	
	public abstract Map<UUID, UserDungeonStatusHistory> getUserDungeonStatusHistorysForIds(List<UUID> ids);
	
}