package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.CombatRoom;

@Component
public class CombatRoomEntityManager extends BaseEntityManager<CombatRoom, UUID>{

	
	
	
	
	public CombatRoomEntityManager() {
		super(CombatRoom.class, UUID.class);
	}



	


}
