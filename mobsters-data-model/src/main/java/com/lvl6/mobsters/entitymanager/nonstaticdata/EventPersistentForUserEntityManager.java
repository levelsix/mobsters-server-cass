package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.EventPersistentForUser;

@Component
public class EventPersistentForUserEntityManager extends BaseEntityManager<EventPersistentForUser, UUID>{

	
	
	
	
	public EventPersistentForUserEntityManager() {
		super(EventPersistentForUser.class, UUID.class);
	}




}
