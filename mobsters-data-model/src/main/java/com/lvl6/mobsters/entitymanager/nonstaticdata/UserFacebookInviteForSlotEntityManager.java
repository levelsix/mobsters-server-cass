package com.lvl6.mobsters.entitymanager.nonstaticdata;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.UserFacebookInviteForSlot;

@Component
public class UserFacebookInviteForSlotEntityManager extends BaseEntityManager<UserFacebookInviteForSlot, UUID>{

	
	
	
	
	public UserFacebookInviteForSlotEntityManager() {
		super(UserFacebookInviteForSlot.class, UUID.class);
	}



}
