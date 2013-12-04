package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class StructureResourceGenerator extends BasePersistentObject<Integer> implements Serializable{


	private static final long serialVersionUID = -575496210438261163L;

	@Id
	protected Integer id = 0;
	
	@Column(name="resource_type")
	@Index
	protected String resourceType = "";
	
	@Column(name="production_rate")
	@Index
	protected float productionRate = 0;
	
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

	public float getProductionRate() {
		return productionRate;
	}

	public void setProductionRate(float productionRate) {
		this.productionRate = productionRate;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return "StructureResourceGenerator [id=" + id + ", resourceType="
				+ resourceType + ", productionRate=" + productionRate
				+ ", capacity=" + capacity + "]";
	}
	
}
