package com.lvl6.mobsters.services.userdungeonstatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.lvl6.mobsters.po.UserDungeonStatus;

public interface UserDungeonStatusService {
		
	public abstract UserDungeonStatus getUserDungeonStatusForId(UUID id);
	
	public abstract Map<UUID, UserDungeonStatus> getUserDungeonStatussForIds(List<UUID> ids);
	
	
	
	

}