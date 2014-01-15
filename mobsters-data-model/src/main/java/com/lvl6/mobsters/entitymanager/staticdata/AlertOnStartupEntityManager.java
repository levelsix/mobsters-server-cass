package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.AlertOnStartup;

@Component
public class AlertOnStartupEntityManager extends BaseEntityManager<AlertOnStartup, Integer>{

	
	
	
	
	public AlertOnStartupEntityManager() {
		super(AlertOnStartup.class, Integer.class);
	}





}
