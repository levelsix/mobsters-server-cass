package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.UserBanned;

@Component
public class UserBannedEntityManager extends BaseEntityManager<UserBanned, Integer>{

	
	
	
	
	public UserBannedEntityManager() {
		super(UserBanned.class, Integer.class);
	}




}
