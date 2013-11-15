package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.UserItem;

@Component
public class UserItemEntityManager extends BaseEntityManager<UserItem, UUID>{

	
	
	
	
	public UserItemEntityManager() {
		super(UserItem.class, UUID.class);
	}




}
