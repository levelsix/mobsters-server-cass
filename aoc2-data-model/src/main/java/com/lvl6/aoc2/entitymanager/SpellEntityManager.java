package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.Spell;

@Component
public class SpellEntityManager extends BaseEntityManager<Spell, UUID>{

	
	
	
	
	public SpellEntityManager() {
		super(Spell.class, UUID.class);
	}


	
	


}
