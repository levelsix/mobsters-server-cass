package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.StructureTownHall;

@Component
public class StructureTownHallEntityManager extends BaseEntityManager<StructureTownHall, Integer>{

	
	
	
	
	public StructureTownHallEntityManager() {
		super(StructureTownHall.class, Integer.class);
	}





}
