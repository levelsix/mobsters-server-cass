package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class StructureResourceStorage extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = 8081038771546831069L;

	@Id
	protected Integer id = 0;
	
	@Column(name="resource_type")
	@Index
	protected String resourceType = "";
	
	@Column(name="capacity")
	@Index
	protected int capacity = 0;

	
	
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return "StructureResourceStorage [id=" + id + ", resourceType="
				+ resourceType + ", capacity=" + capacity + "]";
	}
	
	
}
