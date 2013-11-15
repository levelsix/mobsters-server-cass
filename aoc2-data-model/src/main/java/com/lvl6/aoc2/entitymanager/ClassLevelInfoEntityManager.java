package com.lvl6.aoc2.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.aoc2.po.ClassLevelInfo;

@Component
public class ClassLevelInfoEntityManager extends BaseEntityManager<ClassLevelInfo, UUID>{

	
	
	
	
	public ClassLevelInfoEntityManager() {
		super(ClassLevelInfo.class, UUID.class);
	}



	


}
