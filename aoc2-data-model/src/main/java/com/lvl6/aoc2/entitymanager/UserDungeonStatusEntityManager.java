package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserDungeonStatus;

@Component
public class UserDungeonStatusEntityManager extends BaseEntityManager<UserDungeonStatus, UUID>{

	
	
	
	
	public UserDungeonStatusEntityManager() {
		super(UserDungeonStatus.class, UUID.class);
	}





}
