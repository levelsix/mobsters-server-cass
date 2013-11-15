package com.lvl6.aoc2.po;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.lvl6.aoc2.entitymanager.Index;



@Entity
public class UserSpell extends BasePersistentObject{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="spell_id")
	@Index
	protected UUID spellId = null;
	
	//begin training
	@Column(name="time_acquired")
	@Index
	protected Date timeAcquired = new Date();

	@Column(name="is_training")
	protected Boolean isTraining = false;
	
	@Column(name="level_of_user_when_upgrading")
	protected int levelOfUserWhenUpgrading = 0;
	



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


	public UUID getSpellId() {
		return spellId;
	}


	public void setSpellId(UUID spellId) {
		this.spellId = spellId;
	}


	public Date getTimeAcquired() {
		return timeAcquired;
	}


	public void setTimeAcquired(Date timeAcquired) {
		this.timeAcquired = timeAcquired;
	}


	public Boolean getIsTraining() {
		return isTraining;
	}


	public void setIsTraining(Boolean isTraining) {
		this.isTraining = isTraining;
	}


	public int getLevelOfUserWhenUpgrading() {
		return levelOfUserWhenUpgrading;
	}


	public void setLevelOfUserWhenUpgrading(int levelOfUserWhenUpgrading) {
		this.levelOfUserWhenUpgrading = levelOfUserWhenUpgrading;
	}


	@Override
	public String toString() {
		return "UserSpell [id=" + id + ", userId=" + userId + ", spellId="
				+ spellId + ", timeAcquired=" + timeAcquired + ", isTraining="
				+ isTraining + ", levelOfUserWhenUpgrading="
				+ levelOfUserWhenUpgrading + "]";
	}


	
	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
	
}
