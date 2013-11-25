package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class Structure extends BasePersistentObject<UUID> implements Serializable{

	private static final long serialVersionUID = -487082131666338095L;

	@Id
	protected UUID id = UUID.randomUUID();
	
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
	
	@Column(name="build_time_minutes")
	@Index
	protected int buildTimeMinutes = 0;
	
	@Column(name="build_speedup_base_cost")
	protected int buildSpeedupBaseCost = 0;
	
	@Column(name="prerequisite_town_hall_lvl")
	@Index
	protected int prerequisiteTownHallLvl = 0;

	@Column(name="width")
	protected int width = 0;
	
	@Column(name="height")
	@Index
	protected int height = 0;
	
	@Column(name="sprite_img_name")
	@Index
	protected String spriteImgName = "";
	
	//base cost for researching spell, income, storage, dependent "+tableName()+" building, assume it's in minutes for now
	@Column(name="predecessor_struct_id")
	@Index
	protected int predecessorStructId = 0;
	
	@Column(name="successor_struct_id")
	@Index
	protected int successorStructId = 0;
	
	@Column(name="img_name")
	protected String imgName = "";
	
	@Column(name="img_vertical_pixel_offset")
	protected int imgVerticalPixelOffset = 0;
	
	@Column(name="description")
	protected String description = "";

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

	public int getBuildTimeMinutes() {
		return buildTimeMinutes;
	}

	public void setBuildTimeMinutes(int buildTimeMinutes) {
		this.buildTimeMinutes = buildTimeMinutes;
	}

	public int getBuildSpeedupBaseCost() {
		return buildSpeedupBaseCost;
	}

	public void setBuildSpeedupBaseCost(int buildSpeedupBaseCost) {
		this.buildSpeedupBaseCost = buildSpeedupBaseCost;
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

	public String getSpriteImgName() {
		return spriteImgName;
	}

	public void setSpriteImgName(String spriteImgName) {
		this.spriteImgName = spriteImgName;
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

	public int getImgVerticalPixelOffset() {
		return imgVerticalPixelOffset;
	}

	public void setImgVerticalPixelOffset(int imgVerticalPixelOffset) {
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
				+ buildResourceType + ", buildTimeMinutes=" + buildTimeMinutes
				+ ", buildSpeedupBaseCost=" + buildSpeedupBaseCost
				+ ", prerequisiteTownHallLvl=" + prerequisiteTownHallLvl
				+ ", width=" + width + ", height=" + height
				+ ", spriteImgName=" + spriteImgName + ", predecessorStructId="
				+ predecessorStructId + ", successorStructId="
				+ successorStructId + ", imgName=" + imgName
				+ ", imgVerticalPixelOffset=" + imgVerticalPixelOffset
				+ ", description=" + description + "]";
	}
	
}
