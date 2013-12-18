package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.City;

@Component
public class CityEntityManager extends BaseEntityManager<City, Integer>{

	
	
	
	
	public CityEntityManager() {
		super(City.class, Integer.class);
	}




}
