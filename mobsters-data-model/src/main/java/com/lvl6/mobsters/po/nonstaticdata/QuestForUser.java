package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class QuestForUser extends BasePersistentObject<UUID>{

	//at the moment, can't do composite primary key, hence this key
	//and indexes on userId and questId
	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="quest_id")
	@Index
	protected Integer questId = null;
	
	@Column(name="is_redeemed")
	@Index
	protected boolean isRedeemed = false;
	
	@Column(name="is_complete")
	protected boolean isComplete = false;
	
	@Column(name="progress")
	protected int progress = 0;
	
	@Column(name="time_accepted")
	protected Date timeAccepted = null;
	
	
	
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


	public Integer getQuestId() {
		return questId;
	}


	public void setQuestId(Integer questId) {
		this.questId = questId;
	}


	public boolean isRedeemed() {
		return isRedeemed;
	}


	public void setRedeemed(boolean isRedeemed) {
		this.isRedeemed = isRedeemed;
	}


	public boolean isComplete() {
		return isComplete;
	}


	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}


	public int getProgress() {
		return progress;
	}


	public void setProgress(int progress) {
		this.progress = progress;
	}


	public Date getTimeAccepted() {
		return timeAccepted;
	}


	public void setTimeAccepted(Date timeAccepted) {
		this.timeAccepted = timeAccepted;
	}


	@Override
	public String toString() {
		return "QuestForUser [id=" + id + ", userId=" + userId + ", questId="
				+ questId + ", isRedeemed=" + isRedeemed + ", isComplete="
				+ isComplete + ", progress=" + progress + ", timeAccepted="
				+ timeAccepted + "]";
	}

	
	
}
