package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class Task extends BasePersistentObject<Integer> implements Serializable{
	
	//explicit values are useless, I just made random values

	private static final long serialVersionUID = -349572752778478277L;

	@Id
	protected Integer id = 0;
	
	@Column(name="good_name")
	@Index
	protected String goodName = "";
	
	@Column(name="description")
	@Index
	protected String description = "";
	
	//tasks are tied to cities (a structure in a city)
	@Column(name="city_id")
	@Index
	protected int cityId = 0;
	
	@Column(name="asset_number_within_city")
	@Index
	protected int assetNumberWithinCity = 0;
	
	@Column(name="prerequisite_task_id")
	protected int prerequisiteTaskId = 0;

	@Column(name="prerequisite_quest_id")
	protected int prerequisiteQuestId = 0;
	
	
	
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getAssetNumberWithinCity() {
		return assetNumberWithinCity;
	}

	public void setAssetNumberWithinCity(int assetNumberWithinCity) {
		this.assetNumberWithinCity = assetNumberWithinCity;
	}

	public int getPrerequisiteTaskId() {
		return prerequisiteTaskId;
	}

	public void setPrerequisiteTaskId(int prerequisiteTaskId) {
		this.prerequisiteTaskId = prerequisiteTaskId;
	}

	public int getPrerequisiteQuestId() {
		return prerequisiteQuestId;
	}

	public void setPrerequisiteQuestId(int prerequisiteQuestId) {
		this.prerequisiteQuestId = prerequisiteQuestId;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", goodName=" + goodName + ", description="
				+ description + ", cityId=" + cityId
				+ ", assetNumberWithinCity=" + assetNumberWithinCity
				+ ", prerequisiteTaskId=" + prerequisiteTaskId
				+ ", prerequisiteQuestId=" + prerequisiteQuestId + "]";
	}

}
