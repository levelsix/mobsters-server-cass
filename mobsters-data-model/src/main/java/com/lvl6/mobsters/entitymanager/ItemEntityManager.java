package com.lvl6.mobsters.entitymanager;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.po.Item;

@Component
public class ItemEntityManager extends BaseEntityManager<Item, UUID>{

	
	
	
	
	public ItemEntityManager() {
		super(Item.class, UUID.class);
	}





}
