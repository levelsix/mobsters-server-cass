package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.Monster;

@Component
public class MonsterEntityManager extends BaseEntityManager<Monster, UUID>{

	
	
	
	
	public MonsterEntityManager() {
		super(Monster.class, UUID.class);
	}



	


}
