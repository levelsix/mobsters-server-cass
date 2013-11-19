package com.lvl6.mobsters.po;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class ChestEvent extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="chest_id")
	@Index
	protected UUID chestId = UUID.randomUUID();
	
	@Column(name="start_time")
	@Index
	protected Date startTime = new Date();
	
	@Column(name="end_time")
	@Index
	protected Date endTime = new Date();
	
	@Column(name="event_name")
	@Index
	protected String eventName = "";
	



	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public UUID getChestId() {
		return chestId;
	}


	public void setChestId(UUID chestId) {
		this.chestId = chestId;
	}


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public Date getEndTime() {
		return endTime;
	}


	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public String getEventName() {
		return eventName;
	}


	public void setEventName(String eventName) {
		this.eventName = eventName;
	}


	@Override
	public String toString() {
		return "ChestEvent [id=" + id + ", chestId=" + chestId + ", startTime="
				+ startTime + ", endTime=" + endTime + ", eventName="
				+ eventName + "]";
	}


	
}
