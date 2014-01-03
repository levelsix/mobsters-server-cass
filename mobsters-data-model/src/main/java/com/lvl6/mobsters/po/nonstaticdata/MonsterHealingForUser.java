package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterHealingForUser extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();

	@Column(name="user_id")
	@Index
	protected UUID userId = null;

	//primary key in table monser_for_user
	@Column(name="monster_for_user_id")
	@Index
	protected UUID monsterForUserId = null;

	@Column(name="expected_start_time")
	@Index
	protected Date expectedStartTime = null;

	@Column(name="user_struct_hospital_id")
	protected UUID userStructHospitalId = null;
	
	//how much hp has been healed since last time
	@Column(name="health_progress")
	protected int healthProgress = 0;
	
	@Column(name="priority")
	protected int priority = 0;

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

	public Date getExpectedStartTime() {
		return expectedStartTime;
	}

	public void setExpectedStartTime(Date expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}

	public UUID getUserStructHospitalId() {
		return userStructHospitalId;
	}

	public void setUserStructHospitalId(UUID userStructHospitalId) {
		this.userStructHospitalId = userStructHospitalId;
	}

	public int getHealthProgress() {
		return healthProgress;
	}

	public void setHealthProgress(int healthProgress) {
		this.healthProgress = healthProgress;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	
	
	@Override
	public String toString() {
		return "MonsterHealingForUser [id=" + id + ", userId=" + userId
				+ ", monsterForUserId=" + monsterForUserId
				+ ", expectedStartTime=" + expectedStartTime
				+ ", userStructHospitalId=" + userStructHospitalId
				+ ", healthProgress=" + healthProgress + ", priority="
				+ priority + "]";
	}
	
}
