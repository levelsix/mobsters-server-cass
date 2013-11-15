package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.Consumable;

@Component
public class ConsumableEntityManager extends BaseEntityManager<Consumable, UUID>{

	
	
	
	
	public ConsumableEntityManager() {
		super(Consumable.class, UUID.class);
	}



	


}
