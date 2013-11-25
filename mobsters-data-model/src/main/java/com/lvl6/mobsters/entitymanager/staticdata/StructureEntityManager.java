package com.lvl6.mobsters.entitymanager.staticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.Structure;

@Component
public class StructureEntityManager extends BaseEntityManager<Structure, UUID>{

	
	
	
	
	public StructureEntityManager() {
		super(Structure.class, UUID.class);
	}



}
