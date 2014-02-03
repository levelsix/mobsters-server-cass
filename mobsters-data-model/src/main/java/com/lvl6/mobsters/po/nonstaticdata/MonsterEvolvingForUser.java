package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterEvolvingForUser extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	//primary key in table monser_for_user
	@Column(name="catalyst_mfu_id")
	@Index
	protected UUID catalystMonsterForUserId = null;
	
	//primary key in table monser_for_user
	@Column(name="mfu_id_one")
	@Index
	protected UUID monsterForUserIdOne = null;
	
	//primary key in table monser_for_user
	@Column(name="mfu_id_two")
	@Index
	protected UUID monsterForUserIdTwo = null;
	
	@Column(name="start_time")
	@Index
	protected Date startTime = null;

	
	
	
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

	public UUID getCatalystMonsterForUserId() {
		return catalystMonsterForUserId;
	}

	public void setCatalystMonsterForUserId(UUID catalystMonsterForUserId) {
		this.catalystMonsterForUserId = catalystMonsterForUserId;
	}

	public UUID getMonsterForUserIdOne() {
		return monsterForUserIdOne;
	}

	public void setMonsterForUserIdOne(UUID monsterForUserIdOne) {
		this.monsterForUserIdOne = monsterForUserIdOne;
	}

	public UUID getMonsterForUserIdTwo() {
		return monsterForUserIdTwo;
	}

	public void setMonsterForUserIdTwo(UUID monsterForUserIdTwo) {
		this.monsterForUserIdTwo = monsterForUserIdTwo;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "MonsterEvolvingForUser [id=" + id + ", userId=" + userId
				+ ", catalystMonsterForUserId=" + catalystMonsterForUserId
				+ ", monsterForUserIdOne=" + monsterForUserIdOne
				+ ", monsterForUserIdTwo=" + monsterForUserIdTwo
				+ ", startTime=" + startTime + "]";
	}
	
}
