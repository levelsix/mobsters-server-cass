package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.UserStructureContent;

@Component
public class UserStructureContentEntityManager extends BaseEntityManager<UserStructureContent, UUID>{

	
	
	
	
	public UserStructureContentEntityManager() {
		super(UserStructureContent.class, UUID.class);
	}




}
