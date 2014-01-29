package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.QuestMonsterItem;

@Component
public class QuestMonsterItemEntityManager extends BaseEntityManager<QuestMonsterItem, Integer>{

	
	
	
	
	public QuestMonsterItemEntityManager() {
		super(QuestMonsterItem.class, Integer.class);
	}




}
