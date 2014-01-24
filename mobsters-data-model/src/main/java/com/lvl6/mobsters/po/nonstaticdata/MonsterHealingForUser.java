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

	@Column(name="queued_time")
	@Index
	protected Date queuedTime = null;

//	@Column(name="user_struct_hospital_id")
//	protected UUID userStructHospitalId = null;
	
	//how much hp has been healed since last time
	@Column(name="health_progress")
	protected float healthProgress = 0F;
	
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

	public Date getQueuedTime() {
		return queuedTime;
	}

	public void setQueuedTime(Date queuedTime) {
		this.queuedTime = queuedTime;
	}

	public float getHealthProgress() {
		return healthProgress;
	}

	public void setHealthProgress(float healthProgress) {
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
				+ ", monsterForUserId=" + monsterForUserId + ", queuedTime="
				+ queuedTime + ", healthProgress=" + healthProgress
				+ ", priority=" + priority + "]";
	}
	
}
