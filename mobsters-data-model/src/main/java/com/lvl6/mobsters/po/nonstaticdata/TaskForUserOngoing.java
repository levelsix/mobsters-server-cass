package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class TaskForUserOngoing extends BasePersistentObject<UUID>{

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
	protected int cashGained = 0;
	
	@Column(name="num_revives")
	protected int numRevives = 0;
	
	@Column(name="start_date")
	protected Date startDate = null;

	
	
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public String toString() {
		return "TaskForUserOngoing [id=" + id + ", userId=" + userId
				+ ", taskId=" + taskId + ", expGained=" + expGained
				+ ", cashGained=" + cashGained + ", numRevives=" + numRevives
				+ ", startDate=" + startDate + "]";
	}	
	
}
