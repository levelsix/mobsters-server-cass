package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.MonsterAndRoom;

@Component
public class MonsterAndRoomEntityManager extends BaseEntityManager<MonsterAndRoom, UUID>{

	
	
	
	
	public MonsterAndRoomEntityManager() {
		super(MonsterAndRoom.class, UUID.class);
	}





}
