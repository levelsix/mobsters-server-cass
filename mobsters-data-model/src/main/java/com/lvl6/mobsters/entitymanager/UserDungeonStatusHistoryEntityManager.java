package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.UserDungeonStatusHistory;

@Component
public class UserDungeonStatusHistoryEntityManager extends BaseEntityManager<UserDungeonStatusHistory, UUID>{

	
	
	
	
	public UserDungeonStatusHistoryEntityManager() {
		super(UserDungeonStatusHistory.class, UUID.class);
	}




}
