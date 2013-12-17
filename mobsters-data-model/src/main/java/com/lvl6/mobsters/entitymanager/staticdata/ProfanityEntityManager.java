package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.Profanity;

@Component
public class ProfanityEntityManager extends BaseEntityManager<Profanity, Integer>{

	
	
	
	
	public ProfanityEntityManager() {
		super(Profanity.class, Integer.class);
	}





}
