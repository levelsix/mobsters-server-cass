package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.Quest;

@Component
public class QuestEntityManager extends BaseEntityManager<Quest, Integer>{

	
	
	
	
	public QuestEntityManager() {
		super(Quest.class, Integer.class);
	}





}
