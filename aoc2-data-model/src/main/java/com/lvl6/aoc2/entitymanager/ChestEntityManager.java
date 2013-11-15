package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.Chest;

@Component
public class ChestEntityManager extends BaseEntityManager<Chest, UUID>{

	
	
	
	
	public ChestEntityManager() {
		super(Chest.class, UUID.class);
	}





}
