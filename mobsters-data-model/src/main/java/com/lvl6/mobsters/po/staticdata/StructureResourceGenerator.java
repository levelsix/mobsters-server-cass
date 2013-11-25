package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class StructureResourceGenerator extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = -4801397898325695257L;

	@Id
	protected Integer id = 0;
	
	@Column(name="resource_type")
	@Index
	protected String resource_type = "";
	
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

	public String getResource_type() {
		return resource_type;
	}

	public void setResource_type(String resource_type) {
		this.resource_type = resource_type;
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
		return "StructureResourceGenerator [id=" + id + ", resource_type="
				+ resource_type + ", productionRate=" + productionRate
				+ ", capacity=" + capacity + "]";
	}
	
}
