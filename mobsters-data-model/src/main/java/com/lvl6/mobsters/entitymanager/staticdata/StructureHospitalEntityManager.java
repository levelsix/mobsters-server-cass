package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.StructureHospital;

@Component
public class StructureHospitalEntityManager extends BaseEntityManager<StructureHospital, Integer>{

	
	
	
	
	public StructureHospitalEntityManager() {
		super(StructureHospital.class, Integer.class);
	}



	


}
