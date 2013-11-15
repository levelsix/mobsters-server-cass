package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserSpell;

@Component
public class UserSpellEntityManager extends BaseEntityManager<UserSpell, UUID>{

	
	
	
	
	public UserSpellEntityManager() {
		super(UserSpell.class, UUID.class);
	}




}
