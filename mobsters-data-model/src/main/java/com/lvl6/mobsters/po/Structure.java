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
public class Structure extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
//	@Column(name="structure_id")
//	protected UUID structureId = UUID.randomUUID();
	
	//groups structures
	//(e.g. structure table could contain 9 rows: Inn level 1 to
	//9, with 9 diff row keys. But name would be Inn)
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="lvl")
	protected int lvl = 0;
	
	@Column(name="build_cost")
	@Index
	protected int buildCost = 0;
	
	@Column(name="build_cost_resource_type")
	@Index
	protected int buildCostResourceType = 1;
	
	@Column(name="build_time_seconds")
	@Index
	protected int buildTimeSeconds = 0;
	
	@Column(name="build_speedup_base_cost")
	protected int buildSpeedupBaseCost = 0;
	
	@Column(name="user_lvl_required")
	@Index
	protected int userLvlRequired = 0;

	@Column(name="size")
	protected int size = 0;
	
	@Column(name="functionality_type")
	@Index
	protected int functionalityType = 0;
	
	@Column(name="functionality_resource_type")
	@Index
	protected int functionalityResourceType = 0;
	
	//base cost for researching spell, income, storage, dependent "+tableName()+" building, assume it's in minutes for now
	@Column(name="functionality_value")
	@Index
	protected int functionalityValue = 0;
	
	@Column(name="functionality_capacity")
	@Index
	protected int functionalityCapacity = 0;
	
	@Column(name="functionality_speedup_base_cost")
	protected int functionalitySpeedupBaseCost = 1;
	
	@Column(name="image_name")
	protected String imageName = "";


	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


//	public UUID getStructureId() {
//		return structureId;
//	}
//
//
//	public void setStructureId(UUID structureId) {
//		this.structureId = structureId;
//	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getLvl() {
		return lvl;
	}


	public void setLvl(int lvl) {
		this.lvl = lvl;
	}


	public int getBuildCost() {
		return buildCost;
	}


	public void setBuildCost(int buildCost) {
		this.buildCost = buildCost;
	}


	public int getBuildCostResourceType() {
		return buildCostResourceType;
	}


	public void setBuildCostResourceType(int buildCostResourceType) {
		this.buildCostResourceType = buildCostResourceType;
	}


	public int getBuildTimeSeconds() {
		return buildTimeSeconds;
	}


	public void setBuildTimeSeconds(int buildTimeSeconds) {
		this.buildTimeSeconds = buildTimeSeconds;
	}


	public int getBuildSpeedupBaseCost() {
		return buildSpeedupBaseCost;
	}


	public void setBuildSpeedupBaseCost(int buildSpeedupBaseCost) {
		this.buildSpeedupBaseCost = buildSpeedupBaseCost;
	}


	public int getUserLvlRequired() {
		return userLvlRequired;
	}


	public void setUserLvlRequired(int userLvlRequired) {
		this.userLvlRequired = userLvlRequired;
	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	public int getFunctionalityType() {
		return functionalityType;
	}


	public void setFunctionalityType(int functionalityType) {
		this.functionalityType = functionalityType;
	}


	public int getFunctionalityResourceType() {
		return functionalityResourceType;
	}


	public void setFunctionalityResourceType(int functionalityResourceType) {
		this.functionalityResourceType = functionalityResourceType;
	}


	public int getFunctionalityValue() {
		return functionalityValue;
	}


	public void setFunctionalityValue(int functionalityValue) {
		this.functionalityValue = functionalityValue;
	}


	public int getFunctionalityCapacity() {
		return functionalityCapacity;
	}


	public void setFunctionalityCapacity(int functionalityCapacity) {
		this.functionalityCapacity = functionalityCapacity;
	}


	public int getFunctionalitySpeedupBaseCost() {
		return functionalitySpeedupBaseCost;
	}


	public void setFunctionalitySpeedupBaseCost(int functionalitySpeedupBaseCost) {
		this.functionalitySpeedupBaseCost = functionalitySpeedupBaseCost;
	}


	public String getImageName() {
		return imageName;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}



	@Override
	public String toString() {
		return "Structure [id=" + id + ", name=" + name + ", lvl=" + lvl
				+ ", buildCost=" + buildCost + ", buildCostResourceType="
				+ buildCostResourceType + ", buildTimeSeconds="
				+ buildTimeSeconds + ", buildSpeedupBaseCost="
				+ buildSpeedupBaseCost + ", userLvlRequired=" + userLvlRequired
				+ ", size=" + size + ", functionalityType=" + functionalityType
				+ ", functionalityResourceType=" + functionalityResourceType
				+ ", functionalityValue=" + functionalityValue
				+ ", functionalityCapacity=" + functionalityCapacity
				+ ", functionalitySpeedupBaseCost="
				+ functionalitySpeedupBaseCost + ", imageName=" + imageName
				+ "]";
	}


	
	
}
