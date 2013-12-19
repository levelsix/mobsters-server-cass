package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.ExpansionCost;

@Component
public class ExpansionCostEntityManager extends BaseEntityManager<ExpansionCost, Integer>{

	
	
	
	
	public ExpansionCostEntityManager() {
		super(ExpansionCost.class, Integer.class);
	}


	
	


}
