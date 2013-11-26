package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class Structure extends BasePersistentObject<Integer> implements Serializable{




	/**
	 * 
	 */
	private static final long serialVersionUID = 851303431734583065L;

	@Id
	protected Integer id = 0;
	
	@Column(name="name")
	@Index
	protected String name = "";
	
	@Column(name="lvl")
	protected int lvl = 0;
	
	@Column(name="struct_type")
	@Index
	protected String structType = "";
	
	@Column(name="build_resource_type")
	@Index
	protected String buildResourceType = "";
	
	@Column(name="build_cost")
	protected int buildCost = 0;
	
	@Column(name="build_time_minutes")
	@Index
	protected int buildTimeMinutes = 0;
	
	@Column(name="prerequisite_town_hall_lvl")
	@Index
	protected int prerequisiteTownHallLvl = 0;

	@Column(name="width")
	protected int width = 0;
	
	@Column(name="height")
	@Index
	protected int height = 0;
	
	@Column(name="predecessor_struct_id")
	@Index
	protected int predecessorStructId = 0;
	
	@Column(name="successor_struct_id")
	@Index
	protected int successorStructId = 0;
	
	@Column(name="img_name")
	protected String imgName = "";
	
	@Column(name="img_vertical_pixel_offset")
	protected float imgVerticalPixelOffset = 0;
	
	@Column(name="description")
	protected String description = "";

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getStructType() {
		return structType;
	}

	public void setStructType(String structType) {
		this.structType = structType;
	}

	public String getBuildResourceType() {
		return buildResourceType;
	}

	public void setBuildResourceType(String buildResourceType) {
		this.buildResourceType = buildResourceType;
	}

	public int getBuildCost() {
		return buildCost;
	}

	public void setBuildCost(int buildCost) {
		this.buildCost = buildCost;
	}

	public int getBuildTimeMinutes() {
		return buildTimeMinutes;
	}

	public void setBuildTimeMinutes(int buildTimeMinutes) {
		this.buildTimeMinutes = buildTimeMinutes;
	}

	public int getPrerequisiteTownHallLvl() {
		return prerequisiteTownHallLvl;
	}

	public void setPrerequisiteTownHallLvl(int prerequisiteTownHallLvl) {
		this.prerequisiteTownHallLvl = prerequisiteTownHallLvl;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getPredecessorStructId() {
		return predecessorStructId;
	}

	public void setPredecessorStructId(int predecessorStructId) {
		this.predecessorStructId = predecessorStructId;
	}

	public int getSuccessorStructId() {
		return successorStructId;
	}

	public void setSuccessorStructId(int successorStructId) {
		this.successorStructId = successorStructId;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public float getImgVerticalPixelOffset() {
		return imgVerticalPixelOffset;
	}

	public void setImgVerticalPixelOffset(float imgVerticalPixelOffset) {
		this.imgVerticalPixelOffset = imgVerticalPixelOffset;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Structure [id=" + id + ", name=" + name + ", lvl=" + lvl
				+ ", structType=" + structType + ", buildResourceType="
				+ buildResourceType + ", buildCost=" + buildCost
				+ ", buildTimeMinutes=" + buildTimeMinutes
				+ ", prerequisiteTownHallLvl=" + prerequisiteTownHallLvl
				+ ", width=" + width + ", height=" + height
				+ ", predecessorStructId=" + predecessorStructId
				+ ", successorStructId=" + successorStructId + ", imgName="
				+ imgName + ", imgVerticalPixelOffset="
				+ imgVerticalPixelOffset + ", description=" + description + "]";
	}


}
