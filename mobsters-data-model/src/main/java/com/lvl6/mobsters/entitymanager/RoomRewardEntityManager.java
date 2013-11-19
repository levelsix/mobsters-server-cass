package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.RoomReward;

@Component
public class RoomRewardEntityManager extends BaseEntityManager<RoomReward, UUID>{

	
	
	
	
	public RoomRewardEntityManager() {
		super(RoomReward.class, UUID.class);
	}


	


}
