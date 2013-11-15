package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.MonsterPersistentDrop;

@Component
public class MonsterPersistentDropEntityManager extends BaseEntityManager<MonsterPersistentDrop, UUID>{

	
	
	
	
	public MonsterPersistentDropEntityManager() {
		super(MonsterPersistentDrop.class, UUID.class);
	}




}
