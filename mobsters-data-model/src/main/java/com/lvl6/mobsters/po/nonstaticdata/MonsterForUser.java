package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterForUser extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="monster_id")
	@Index
	protected int monsterId = 0;
	
	@Column(name="current_exp")
	protected int currentExp = 0;
	
	@Column(name="current_lvl")
	@Index
	protected int currentLvl = 0;
	
	@Column(name="current_health")
	@Index
	protected int currentHealth = 0;
	
	@Column(name="num_pieces")
	@Index
	protected int numPieces = 0;
	
	@Column(name="is_complete")
	protected boolean isComplete = false;
	
	@Column(name="combine_start_time")
	protected Date combineStartTime = null;

	@Column(name="team_slot_num")
	protected int teamSlotNum = 0;
	
	@Column(name="source_of_pieces")
	protected String sourceOfPieces = null;
	
	

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

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getCurrentExp() {
		return currentExp;
	}

	public void setCurrentExp(int currentExp) {
		this.currentExp = currentExp;
	}

	public int getCurrentLvl() {
		return currentLvl;
	}

	public void setCurrentLvl(int currentLvl) {
		this.currentLvl = currentLvl;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	public int getNumPieces() {
		return numPieces;
	}

	public void setNumPieces(int numPieces) {
		this.numPieces = numPieces;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public Date getCombineStartTime() {
		return combineStartTime;
	}

	public void setCombineStartTime(Date combineStartTime) {
		this.combineStartTime = combineStartTime;
	}

	public int getTeamSlotNum() {
		return teamSlotNum;
	}

	public void setTeamSlotNum(int teamSlotNum) {
		this.teamSlotNum = teamSlotNum;
	}

	public String getSourceOfPieces() {
		return sourceOfPieces;
	}

	public void setSourceOfPieces(String sourceOfPieces) {
		this.sourceOfPieces = sourceOfPieces;
	}

	@Override
	public String toString() {
		return "MonsterForUser [id=" + id + ", userId=" + userId
				+ ", monsterId=" + monsterId + ", currentExp=" + currentExp
				+ ", currentLvl=" + currentLvl + ", currentHealth="
				+ currentHealth + ", numPieces=" + numPieces + ", isComplete="
				+ isComplete + ", combineStartTime=" + combineStartTime
				+ ", teamSlotNum=" + teamSlotNum + ", sourceOfPieces="
				+ sourceOfPieces + "]";
	}
}
