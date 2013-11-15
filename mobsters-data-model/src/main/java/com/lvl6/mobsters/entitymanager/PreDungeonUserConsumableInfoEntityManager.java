package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.PreDungeonUserConsumableInfo;

@Component
public class PreDungeonUserConsumableInfoEntityManager extends BaseEntityManager<PreDungeonUserConsumableInfo, UUID>{

	
	
	
	
	public PreDungeonUserConsumableInfoEntityManager() {
		super(PreDungeonUserConsumableInfo.class, UUID.class);
	}



	


}
