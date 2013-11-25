package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.StructureLab;

@Component
public class StructureLabEntityManager extends BaseEntityManager<StructureLab, Integer>{

	
	
	
	
	public StructureLabEntityManager() {
		super(StructureLab.class, Integer.class);
	}


	


}
