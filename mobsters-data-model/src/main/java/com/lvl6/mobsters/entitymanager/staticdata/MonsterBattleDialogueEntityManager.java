package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.MonsterBattleDialogue;

@Component
public class MonsterBattleDialogueEntityManager extends BaseEntityManager<MonsterBattleDialogue, Integer>{

	
	
	
	
	public MonsterBattleDialogueEntityManager() {
		super(MonsterBattleDialogue.class, Integer.class);
	}




}
