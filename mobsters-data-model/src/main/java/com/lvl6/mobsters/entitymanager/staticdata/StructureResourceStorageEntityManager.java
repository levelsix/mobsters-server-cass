package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.StructureResourceStorage;

@Component
public class StructureResourceStorageEntityManager extends BaseEntityManager<StructureResourceStorage, Integer>{

	
	
	
	
	public StructureResourceStorageEntityManager() {
		super(StructureResourceStorage.class, Integer.class);
	}



	


}
