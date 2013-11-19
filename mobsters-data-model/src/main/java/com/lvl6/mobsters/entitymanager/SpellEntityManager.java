package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.Spell;

@Component
public class SpellEntityManager extends BaseEntityManager<Spell, UUID>{

	
	
	
	
	public SpellEntityManager() {
		super(Spell.class, UUID.class);
	}


	
	


}
