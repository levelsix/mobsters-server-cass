package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.BoosterPack;

@Component
public class BoosterPackEntityManager extends BaseEntityManager<BoosterPack, Integer>{

	
	
	
	
	public BoosterPackEntityManager() {
		super(BoosterPack.class, Integer.class);
	}




}
