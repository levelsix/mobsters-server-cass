package com.lvl6.mobsters.po;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class Item extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="item_type")
	protected int itemType = 0;
	


	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getItemType() {
		return itemType;
	}


	public void setItemType(int itemType) {
		this.itemType = itemType;
	}




	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", itemType=" + itemType
				+ "]";
	}

	
		
}
