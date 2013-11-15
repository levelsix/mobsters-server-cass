package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.UserSpell;

@Component
public class UserSpellEntityManager extends BaseEntityManager<UserSpell, UUID>{

	
	
	
	
	public UserSpellEntityManager() {
		super(UserSpell.class, UUID.class);
	}




}
