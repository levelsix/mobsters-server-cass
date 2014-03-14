package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.Clan;

@Component
public class ClanEntityManager extends BaseEntityManager<Clan, UUID>{
	
	public ClanEntityManager() {
		super(Clan.class, UUID.class);
	}


}
