package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.BoosterItem;

@Component
public class BoosterItemEntityManager extends BaseEntityManager<BoosterItem, Integer>{

	
	
	
	
	public BoosterItemEntityManager() {
		super(BoosterItem.class, Integer.class);
	}


	


}
