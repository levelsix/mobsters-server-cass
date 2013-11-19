package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.PreDungeonUserEquipInfo;

@Component
public class PreDungeonUserEquipInfoEntityManager extends BaseEntityManager<PreDungeonUserEquipInfo, UUID>{

	
	
	
	
	public PreDungeonUserEquipInfoEntityManager() {
		super(PreDungeonUserEquipInfo.class, UUID.class);
	}





}
