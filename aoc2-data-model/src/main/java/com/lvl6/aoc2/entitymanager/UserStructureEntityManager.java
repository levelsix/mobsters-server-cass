package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserStructure;

@Component
public class UserStructureEntityManager extends BaseEntityManager<UserStructure, UUID>{

	
	
	
	
	public UserStructureEntityManager() {
		super(UserStructure.class, UUID.class);
	}




}
