package com.lvl6.mobsters.po;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class UserConsumableQueue extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="consumable_id")
	@Index
	protected UUID consumableId = null;
	
	@Column(name="quantity")
	protected int quantity = 0;
	
	@Column(name="expected_start")
	@Index
	protected Date expectedStart = null;
	
	@Column(name="entered_queue")
	@Index
	protected Date enteredQueue = null;

	@Column(name="is_finished_building")
	protected boolean isFinishedBuilding = false;

	

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


	public UUID getConsumableId() {
		return consumableId;
	}


	public void setConsumableId(UUID consumableId) {
		this.consumableId = consumableId;
	}


	public int getQuantity() {
		return quantity;
	}


	public void setQuantity(int quantity) {
		this.quantity = quantity;
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


	public boolean isFinishedBuilding() {
		return isFinishedBuilding;
	}


	public void setFinishedBuilding(boolean isFinishedBuilding) {
		this.isFinishedBuilding = isFinishedBuilding;
	}


	@Override
	public String toString() {
		return "UserConsumableQueue [id=" + id + ", userId=" + userId
				+ ", consumableId=" + consumableId + ", quantity=" + quantity
				+ ", expectedStart=" + expectedStart + ", enteredQueue="
				+ enteredQueue + ", isFinishedBuilding=" + isFinishedBuilding
				+ "]";
	}

	
	
	
}
