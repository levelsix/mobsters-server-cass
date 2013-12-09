package com.lvl6.mobsters.entitymanager.staticdata;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.entitymanager.BaseEntityManager;
import com.lvl6.mobsters.po.staticdata.Task;

@Component
public class TaskEntityManager extends BaseEntityManager<Task, Integer>{

	
	
	
	
	public TaskEntityManager() {
		super(Task.class, Integer.class);
	}





}
