package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.UserCurrencyHistory;

@Component
public class UserCurrencyHistoryEntityManager extends BaseEntityManager<UserCurrencyHistory, UUID>{

	
	
	
	
	public UserCurrencyHistoryEntityManager() {
		super(UserCurrencyHistory.class, UUID.class);
	}




}
