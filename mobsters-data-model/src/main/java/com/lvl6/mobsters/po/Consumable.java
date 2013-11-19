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
public class Consumable extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="functionality_type")
	@Index
	protected int functionalityType = 0;
	
	//(if (Float: < 1) ? percent : flat) 
	@Column(name="functionality_constant")
	protected double functionalityConstant = 0;
	
	@Column(name="cost")
	@Index
	protected int cost = 0;
	
	@Column(name="cost_resouce_type")
	protected int costResourceType = 0;
	
	@Column(name="max_limit")
	protected int maxLimit = 0;
	
	@Column(name="create_time_seconds")
	protected int createTimeSeconds = 0;

	@Column(name="base_speedup_cost")
	protected int baseSpeedupCost = 0;
	
	
	
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


	public int getFunctionalityType() {
		return functionalityType;
	}


	public void setFunctionalityType(int functionalityType) {
		this.functionalityType = functionalityType;
	}


	public double getFunctionalityConstant() {
		return functionalityConstant;
	}


	public void setFunctionalityConstant(double functionalityConstant) {
		this.functionalityConstant = functionalityConstant;
	}


	public int getCost() {
		return cost;
	}


	public void setCost(int cost) {
		this.cost = cost;
	}


	public int getCostResourceType() {
		return costResourceType;
	}


	public void setCostResourceType(int costResourceType) {
		this.costResourceType = costResourceType;
	}


	public int getMaxLimit() {
		return maxLimit;
	}


	public void setMaxLimit(int maxLimit) {
		this.maxLimit = maxLimit;
	}


	public int getCreateTimeSeconds() {
		return createTimeSeconds;
	}


	public void setCreateTimeSeconds(int createTimeSeconds) {
		this.createTimeSeconds = createTimeSeconds;
	}


	public int getBaseSpeedupCost() {
		return baseSpeedupCost;
	}


	public void setBaseSpeedupCost(int baseSpeedupCost) {
		this.baseSpeedupCost = baseSpeedupCost;
	}




	@Override
	public String toString() {
		return "Consumable [id=" + id + ", name=" + name
				+ ", functionalityType=" + functionalityType
				+ ", functionalityConstant=" + functionalityConstant
				+ ", cost=" + cost + ", costResourceType=" + costResourceType
				+ ", maxLimit=" + maxLimit + ", createTimeSeconds="
				+ createTimeSeconds + ", baseSpeedupCost=" + baseSpeedupCost
				+ "]";
	}

	
	
}
