package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterEnhancingHistory extends BasePersistentObject<UUID>{


	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="monster_enhancing_for_user_id")
	protected UUID monsterEnhancingForUserId = null;
	
	//enhancing id of monster this monster was sacrificed for, otherwise the enhancing
	//id of the base monster (which is this monster, so enhancingStartTime is null, and
	//monsterEnhancingForUserId would be the same)
	@Column(name="base_enhancing_id")
	protected UUID baseEnhancingId = null;
	
	@Column(name="time_of_entry")
	protected Date timeOfEntry = null;
	
	//a null enhancing_start_time indicates that current monster is the one being enhanced
	@Column(name="enhancing_start_time")
	@Index
	protected Date enhancingStartTime = null;
	
	//primary key in table monser_for_user
	@Column(name="monster_for_user_id")
	@Index
	protected UUID monsterForUserId = null;
	
	@Column(name="monster_id")
	protected int monsterId = 0;
	
	//curExp and prevExp don't mean anything for the feeder monsters
	@Column(name="cur_exp")
	protected int curExp = 0;
	
	@Column(name="prev_exp")
	protected int prevExp = 0;
	
	@Column(name="enhancing_cancelled")
	protected boolean enhancingCancelled = false;

	
	
	
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

	public UUID getMonsterEnhancingForUserId() {
		return monsterEnhancingForUserId;
	}

	public void setMonsterEnhancingForUserId(UUID monsterEnhancingForUserId) {
		this.monsterEnhancingForUserId = monsterEnhancingForUserId;
	}

	public UUID getBaseEnhancingId() {
		return baseEnhancingId;
	}

	public void setBaseEnhancingId(UUID baseEnhancingId) {
		this.baseEnhancingId = baseEnhancingId;
	}

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	public Date getEnhancingStartTime() {
		return enhancingStartTime;
	}

	public void setEnhancingStartTime(Date enhancingStartTime) {
		this.enhancingStartTime = enhancingStartTime;
	}

	public UUID getMonsterForUserId() {
		return monsterForUserId;
	}

	public void setMonsterForUserId(UUID monsterForUserId) {
		this.monsterForUserId = monsterForUserId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getCurExp() {
		return curExp;
	}

	public void setCurExp(int curExp) {
		this.curExp = curExp;
	}

	public int getPrevExp() {
		return prevExp;
	}

	public void setPrevExp(int prevExp) {
		this.prevExp = prevExp;
	}

	public boolean isEnhancingCancelled() {
		return enhancingCancelled;
	}

	public void setEnhancingCancelled(boolean enhancingCancelled) {
		this.enhancingCancelled = enhancingCancelled;
	}

	@Override
	public String toString() {
		return "MonsterEnhancingHistory [id=" + id + ", userId=" + userId
				+ ", monsterEnhancingForUserId=" + monsterEnhancingForUserId
				+ ", baseEnhancingId=" + baseEnhancingId + ", timeOfEntry="
				+ timeOfEntry + ", enhancingStartTime=" + enhancingStartTime
				+ ", monsterForUserId=" + monsterForUserId + ", monsterId="
				+ monsterId + ", curExp=" + curExp + ", prevExp=" + prevExp
				+ ", enhancingCancelled=" + enhancingCancelled + "]";
	}

}
