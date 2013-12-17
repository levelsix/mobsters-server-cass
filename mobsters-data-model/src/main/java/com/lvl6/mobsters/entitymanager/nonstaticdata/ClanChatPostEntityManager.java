package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.ClanChatPost;

@Component
public class ClanChatPostEntityManager extends BaseEntityManager<ClanChatPost, UUID>{
	
	public ClanChatPostEntityManager() {
		super(ClanChatPost.class, UUID.class);
	}


}
