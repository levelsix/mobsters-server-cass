package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.PreDungeonUserEquipInfo;

@Component
public class PreDungeonUserEquipInfoEntityManager extends BaseEntityManager<PreDungeonUserEquipInfo, UUID>{

	
	
	
	
	public PreDungeonUserEquipInfoEntityManager() {
		super(PreDungeonUserEquipInfo.class, UUID.class);
	}





}
