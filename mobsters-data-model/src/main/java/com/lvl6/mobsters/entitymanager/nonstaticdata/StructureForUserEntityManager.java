package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;

@Component
public class StructureForUserEntityManager extends BaseEntityManager<StructureForUser, UUID>{

	
	
	
	
	public StructureForUserEntityManager() {
		super(StructureForUser.class, UUID.class);
	}




}
