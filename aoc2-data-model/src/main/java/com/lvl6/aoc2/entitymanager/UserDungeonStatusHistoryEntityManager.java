package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserDungeonStatusHistory;

@Component
public class UserDungeonStatusHistoryEntityManager extends BaseEntityManager<UserDungeonStatusHistory, UUID>{

	
	
	
	
	public UserDungeonStatusHistoryEntityManager() {
		super(UserDungeonStatusHistory.class, UUID.class);
	}




}
