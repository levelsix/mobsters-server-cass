package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.EventPersistent;

@Component
public class EventPersistentEntityManager extends BaseEntityManager<EventPersistent, Integer>{

	
	
	
	
	public EventPersistentEntityManager() {
		super(EventPersistent.class, Integer.class);
	}


	
	


}
