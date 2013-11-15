package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.UserDevice;

@Component
public class UserDeviceEntityManager extends BaseEntityManager<UserDevice, UUID>{

	
	
	
	
	public UserDeviceEntityManager() {
		super(UserDevice.class, UUID.class);
	}





}
