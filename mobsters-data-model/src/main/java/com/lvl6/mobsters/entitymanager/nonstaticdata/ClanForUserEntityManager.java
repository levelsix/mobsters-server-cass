package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;

@Component
public class ClanForUserEntityManager extends BaseEntityManager<ClanForUser, UUID>{
	
	public ClanForUserEntityManager() {
		super(ClanForUser.class, UUID.class);
	}


}
