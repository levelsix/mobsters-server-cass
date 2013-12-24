package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterForUserDeleted extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="monster_id")
	@Index
	protected int monsterId = 0;
	
	@Column(name="exp")
	protected int exp = 0;
	
	@Column(name="lvl")
	@Index
	protected int lvl = 0;
	
	@Column(name="health")
	@Index
	protected int health = 0;
	
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
	protected String sourceOfPieces = "";
	
	@Column(name="deleted_reason")
	protected String deletedReason = "";
	
	@Column(name="details")
	protected String details = "";
	
	@Column(name="deleted_time")
	protected Date deletedTime = null;
	
	

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

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		this.lvl = lvl;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
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

	public String getDeletedReason() {
		return deletedReason;
	}

	public void setDeletedReason(String deletedReason) {
		this.deletedReason = deletedReason;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Date getDeletedTime() {
		return deletedTime;
	}

	public void setDeletedTime(Date deletedTime) {
		this.deletedTime = deletedTime;
	}

	@Override
	public String toString() {
		return "MonsterForUserDeleted [id=" + id + ", userId=" + userId
				+ ", monsterId=" + monsterId + ", exp=" + exp + ", lvl=" + lvl
				+ ", health=" + health + ", numPieces=" + numPieces
				+ ", isComplete=" + isComplete + ", combineStartTime="
				+ combineStartTime + ", teamSlotNum=" + teamSlotNum
				+ ", sourceOfPieces=" + sourceOfPieces + ", deletedReason="
				+ deletedReason + ", details=" + details + ", deletedTime="
				+ deletedTime + "]";
	}

}
