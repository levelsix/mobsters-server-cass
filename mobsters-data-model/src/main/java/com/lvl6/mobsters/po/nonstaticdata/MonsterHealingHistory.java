package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterHealingHistory extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();

	@Column(name="user_id")
	@Index
	protected UUID userId = null;

	//primary key in table monser_for_user
	@Column(name="monster_for_user_id")
	@Index
	protected UUID monsterForUserId = null;

	@Column(name="monster_id")
	protected int monsterId = 0;
	
	@Column(name="time_of_entry")
	@Index
	protected Date timeOfEntry = null;
	
	@Column(name="healing_start_time")
	@Index
	protected Date healingStartTime = null;
	
	@Column(name="user_struct_hospital_id")
	protected UUID userStructHospitalId = null;
	
	@Column(name="cur_health")
	protected int curHealth = 0;
	
	@Column(name="prev_health")
	protected int prevHealth = 0;
	
	@Column(name="healing_cancelled")
	protected boolean healingCancelled = false;

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

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	public Date getHealingStartTime() {
		return healingStartTime;
	}

	public void setHealingStartTime(Date healingStartTime) {
		this.healingStartTime = healingStartTime;
	}

	public UUID getUserStructHospitalId() {
		return userStructHospitalId;
	}

	public void setUserStructHospitalId(UUID userStructHospitalId) {
		this.userStructHospitalId = userStructHospitalId;
	}

	public int getCurHealth() {
		return curHealth;
	}

	public void setCurHealth(int curHealth) {
		this.curHealth = curHealth;
	}

	public int getPrevHealth() {
		return prevHealth;
	}

	public void setPrevHealth(int prevHealth) {
		this.prevHealth = prevHealth;
	}

	public boolean isHealingCancelled() {
		return healingCancelled;
	}

	public void setHealingCancelled(boolean healingCancelled) {
		this.healingCancelled = healingCancelled;
	}

	@Override
	public String toString() {
		return "MonsterHealingHistory [id=" + id + ", userId=" + userId
				+ ", monsterForUserId=" + monsterForUserId + ", monsterId="
				+ monsterId + ", timeOfEntry=" + timeOfEntry
				+ ", healingStartTime=" + healingStartTime
				+ ", userStructHospitalId=" + userStructHospitalId
				+ ", curHealth=" + curHealth + ", prevHealth=" + prevHealth
				+ ", healingCancelled=" + healingCancelled + "]";
	}
	
}
