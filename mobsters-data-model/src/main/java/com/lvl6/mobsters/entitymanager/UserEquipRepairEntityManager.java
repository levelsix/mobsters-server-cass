package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.UserEquipRepair;

@Component
public class UserEquipRepairEntityManager extends BaseEntityManager<UserEquipRepair, UUID>{

	
	
	
	
	public UserEquipRepairEntityManager() {
		super(UserEquipRepair.class, UUID.class);
	}




}
