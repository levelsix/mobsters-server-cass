package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.StructureResourceGenerator;

@Component
public class StructureResourceGeneratorEntityManager extends BaseEntityManager<StructureResourceGenerator, Integer>{

	
	
	
	
	public StructureResourceGeneratorEntityManager() {
		super(StructureResourceGenerator.class, Integer.class);
	}


	
	


}
