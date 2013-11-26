package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.StructureResidence;

@Component
public class StructureResidenceEntityManager extends BaseEntityManager<StructureResidence, Integer>{

	
	
	
	
	public StructureResidenceEntityManager() {
		super(StructureResidence.class, Integer.class);
	}





}
