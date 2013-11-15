package com.lvl6.mobsters.po;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.lvl6.mobsters.entitymanager.Index;



@Entity
public class UserEquipRepair extends BasePersistentObject{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="equip_id")
	@Index
	protected UUID equipId = null;
	
	@Column(name="durability")
	protected double durability = 0;
	
	@Column(name="expected_start")
	@Index
	protected Date expectedStart = null;
	
	@Column(name="entered_queue")
	@Index
	protected Date enteredQueue = null;
	
	//keeping track of when the user first got this equip
	@Column(name="time_acquired")
	@Index
	protected Date timeAcquired = new Date();
	
	@Column(name="level_of_user_when_acquired")
	protected int levelOfUserWhenAcquired = 0;
	
	@Column(name="dungeon_room_or_chest_acquired_from")
	protected String dungeonRoomOrChestAcquiredFrom = "";


	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public UUID getUserId() {
		return userId;
	}


	public void setUserId(UUID userId) {
		this.userId = userId;
	}


	public UUID getEquipId() {
		return equipId;
	}


	public void setEquipId(UUID equipId) {
		this.equipId = equipId;
	}


	public double getDurability() {
		return durability;
	}


	public void setDurability(double durability) {
		this.durability = durability;
	}


	public Date getExpectedStart() {
		return expectedStart;
	}


	public void setExpectedStart(Date expectedStart) {
		this.expectedStart = expectedStart;
	}


	public Date getEnteredQueue() {
		return enteredQueue;
	}


	public void setEnteredQueue(Date enteredQueue) {
		this.enteredQueue = enteredQueue;
	}


	public Date getTimeAcquired() {
		return timeAcquired;
	}


	public void setTimeAcquired(Date timeAcquired) {
		this.timeAcquired = timeAcquired;
	}


	public int getLevelOfUserWhenAcquired() {
		return levelOfUserWhenAcquired;
	}


	public void setLevelOfUserWhenAcquired(int levelOfUserWhenAcquired) {
		this.levelOfUserWhenAcquired = levelOfUserWhenAcquired;
	}


	public String getDungeonRoomOrChestAcquiredFrom() {
		return dungeonRoomOrChestAcquiredFrom;
	}


	public void setDungeonRoomOrChestAcquiredFrom(
			String dungeonRoomOrChestAcquiredFrom) {
		this.dungeonRoomOrChestAcquiredFrom = dungeonRoomOrChestAcquiredFrom;
	}


	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
	
}
