package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.RoomReward;

@Component
public class RoomRewardEntityManager extends BaseEntityManager<RoomReward, UUID>{

	
	
	
	
	public RoomRewardEntityManager() {
		super(RoomReward.class, UUID.class);
	}


	


}
