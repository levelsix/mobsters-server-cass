package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.Structure;

@Component
public class StructureEntityManager extends BaseEntityManager<Structure, UUID>{

	
	
	
	
	public StructureEntityManager() {
		super(Structure.class, UUID.class);
	}



}
