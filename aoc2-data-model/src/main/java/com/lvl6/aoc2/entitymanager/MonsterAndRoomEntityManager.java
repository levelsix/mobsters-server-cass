package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.MonsterAndRoom;

@Component
public class MonsterAndRoomEntityManager extends BaseEntityManager<MonsterAndRoom, UUID>{

	
	
	
	
	public MonsterAndRoomEntityManager() {
		super(MonsterAndRoom.class, UUID.class);
	}





}
