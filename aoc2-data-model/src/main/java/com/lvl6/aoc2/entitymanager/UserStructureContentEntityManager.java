package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserStructureContent;

@Component
public class UserStructureContentEntityManager extends BaseEntityManager<UserStructureContent, UUID>{

	
	
	
	
	public UserStructureContentEntityManager() {
		super(UserStructureContent.class, UUID.class);
	}




}
