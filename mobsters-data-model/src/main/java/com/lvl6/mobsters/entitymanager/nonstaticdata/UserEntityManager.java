package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;

@Component
public class UserEntityManager extends BaseEntityManager<User, UUID>{
	
	public UserEntityManager() {
		super(User.class, UUID.class);
	}




}
