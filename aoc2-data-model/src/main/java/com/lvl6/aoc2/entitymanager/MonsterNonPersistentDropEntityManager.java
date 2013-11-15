package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.MonsterNonPersistentDrop;

@Component
public class MonsterNonPersistentDropEntityManager extends BaseEntityManager<MonsterNonPersistentDrop, UUID>{

	
	
	
	
	public MonsterNonPersistentDropEntityManager() {
		super(MonsterNonPersistentDrop.class, UUID.class);
	}





}
