package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;

@Component
public class QuestForUserEntityManager extends BaseEntityManager<QuestForUser, UUID>{

	
	
	
	
	public QuestForUserEntityManager() {
		super(QuestForUser.class, UUID.class);
	}




}
