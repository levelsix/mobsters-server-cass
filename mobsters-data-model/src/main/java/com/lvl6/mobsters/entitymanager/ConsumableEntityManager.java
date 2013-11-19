package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.Consumable;

@Component
public class ConsumableEntityManager extends BaseEntityManager<Consumable, UUID>{

	
	
	
	
	public ConsumableEntityManager() {
		super(Consumable.class, UUID.class);
	}



	


}
