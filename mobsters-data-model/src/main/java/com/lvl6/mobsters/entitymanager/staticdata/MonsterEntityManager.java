package com.lvl6.mobsters.entitymanager.staticdata;


import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.Monster;

@Component
public class MonsterEntityManager extends BaseEntityManager<Monster, Integer>{

	
	
	
	
	public MonsterEntityManager() {
		super(Monster.class, Integer.class);
	}





}
