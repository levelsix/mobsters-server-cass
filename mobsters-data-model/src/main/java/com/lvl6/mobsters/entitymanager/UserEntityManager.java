package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.User;

@Component
public class UserEntityManager extends BaseEntityManager<User, UUID>{
	
	public UserEntityManager() {
		super(User.class, UUID.class);
	}




}
