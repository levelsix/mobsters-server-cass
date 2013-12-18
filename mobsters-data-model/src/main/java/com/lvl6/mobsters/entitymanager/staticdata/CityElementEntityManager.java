package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.CityElement;

@Component
public class CityElementEntityManager extends BaseEntityManager<CityElement, Integer>{

	
	
	
	
	public CityElementEntityManager() {
		super(CityElement.class, Integer.class);
	}


	


}
