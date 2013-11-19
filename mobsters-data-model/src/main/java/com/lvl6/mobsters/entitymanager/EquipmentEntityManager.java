package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.Equipment;

@Component
public class EquipmentEntityManager extends BaseEntityManager<Equipment, UUID>{

	
	
	
	
	public EquipmentEntityManager() {
		super(Equipment.class, UUID.class);
	}


	


}
