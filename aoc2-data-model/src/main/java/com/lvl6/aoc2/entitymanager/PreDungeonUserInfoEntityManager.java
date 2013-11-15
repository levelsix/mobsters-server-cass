package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.PreDungeonUserInfo;

@Component
public class PreDungeonUserInfoEntityManager extends BaseEntityManager<PreDungeonUserInfo, UUID>{

	
	
	
	
	public PreDungeonUserInfoEntityManager() {
		super(PreDungeonUserInfo.class, UUID.class);
	}


	


}
