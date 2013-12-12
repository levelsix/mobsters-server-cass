package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class TaskHistory extends BasePersistentObject<UUID>{

	//this is the id from task_for_user_ongoing table
	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="task_id")
	@Index
	protected int taskId = 0;
	
	@Column(name="exp_gained")
	protected int expGained = 0;
	
	@Column(name="cash_gained")
	@Index
	protected int cashGained = 0;
	
	@Column(name="num_revives")
	@Index
	protected int numRevives = 0;

	@Column(name="start_time")
	protected Date startTime = null;
	
	@Column(name="end_time")
	protected Date endTime = null;
	
	@Column(name="user_won")
	protected boolean userWon = false;
	
	@Column(name="cancelled")
	protected boolean cancelled = false;
	

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

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getExpGained() {
		return expGained;
	}

	public void setExpGained(int expGained) {
		this.expGained = expGained;
	}

	public int getCashGained() {
		return cashGained;
	}

	public void setCashGained(int cashGained) {
		this.cashGained = cashGained;
	}

	public int getNumRevives() {
		return numRevives;
	}

	public void setNumRevives(int numRevives) {
		this.numRevives = numRevives;
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

	public boolean isUserWon() {
		return userWon;
	}

	public void setUserWon(boolean userWon) {
		this.userWon = userWon;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public String toString() {
		return "TaskHistory [id=" + id + ", userId=" + userId + ", taskId="
				+ taskId + ", expGained=" + expGained + ", cashGained="
				+ cashGained + ", numRevives=" + numRevives + ", startTime="
				+ startTime + ", endTime=" + endTime + ", userWon=" + userWon
				+ ", cancelled=" + cancelled + "]";
	}
}
