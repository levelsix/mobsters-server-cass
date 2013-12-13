package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.BoosterDisplayItem;

@Component
public class BoosterDisplayItemEntityManager extends BaseEntityManager<BoosterDisplayItem, Integer>{

	
	
	
	
	public BoosterDisplayItemEntityManager() {
		super(BoosterDisplayItem.class, Integer.class);
	}


	


}
