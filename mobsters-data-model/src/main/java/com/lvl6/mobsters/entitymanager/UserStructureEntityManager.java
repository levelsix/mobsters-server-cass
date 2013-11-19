package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.UserStructure;

@Component
public class UserStructureEntityManager extends BaseEntityManager<UserStructure, UUID>{

	
	
	
	
	public UserStructureEntityManager() {
		super(UserStructure.class, UUID.class);
	}




}
