package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterEnhancingForUser extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	//primary key in table monser_for_user
	@Column(name="monster_for_user_id")
	@Index
	protected UUID monsterForUserId = null;
	
	//a null expected_start_time indicates that current monster is the one being enhanced
	@Column(name="expected_start_time")
	@Index
	protected Date expectedStartTime = null;

	@Column(name="enhancing_cost")
	protected int enhancingCost = 0;

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

	public int getEnhancingCost() {
		return enhancingCost;
	}

	public void setEnhancingCost(int enhancingCost) {
		this.enhancingCost = enhancingCost;
	}

	@Override
	public String toString() {
		return "MonsterEnhancingForUser [id=" + id + ", userId=" + userId
				+ ", monsterForUserId=" + monsterForUserId
				+ ", expectedStartTime=" + expectedStartTime
				+ ", enhancingCost=" + enhancingCost + "]";
	}
	
}
