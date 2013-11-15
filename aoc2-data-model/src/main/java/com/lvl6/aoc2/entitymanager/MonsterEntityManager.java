package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.Monster;

@Component
public class MonsterEntityManager extends BaseEntityManager<Monster, UUID>{

	
	
	
	
	public MonsterEntityManager() {
		super(Monster.class, UUID.class);
	}



	


}
