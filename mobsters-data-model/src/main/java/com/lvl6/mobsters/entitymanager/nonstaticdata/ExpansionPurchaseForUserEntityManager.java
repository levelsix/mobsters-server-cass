package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.ExpansionPurchaseForUser;

@Component
public class ExpansionPurchaseForUserEntityManager extends BaseEntityManager<ExpansionPurchaseForUser, UUID>{

	
	
	
	
	public ExpansionPurchaseForUserEntityManager() {
		super(ExpansionPurchaseForUser.class, UUID.class);
	}




}
