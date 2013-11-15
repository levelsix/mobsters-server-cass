package com.lvl6.mobsters.po;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.lvl6.mobsters.entitymanager.Index;



@Entity
public class PreDungeonUserInfo extends BasePersistentObject{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = UUID.randomUUID();
	
	@Column(name="health")
	protected int health = 0;
	
	@Column(name="mana")
	protected int mana = 0;
	
	@Column(name="combat_room_id")
	@Index
	protected UUID combatRoomId = null;
	
	@Column(name="level_of_user")
	protected int levelOfUser = 0;

	@Column(name="time_user_enters_dungeon")
	@Index
	protected Date timeUserEntersDungeon = new Date();
	


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


	public int getHealth() {
		return health;
	}


	public void setHealth(int health) {
		this.health = health;
	}


	public int getMana() {
		return mana;
	}


	public void setMana(int mana) {
		this.mana = mana;
	}


	public UUID getCombatRoomId() {
		return combatRoomId;
	}


	public void setCombatRoomId(UUID combatRoomId) {
		this.combatRoomId = combatRoomId;
	}


	public int getLevelOfUser() {
		return levelOfUser;
	}


	public void setLevelOfUser(int levelOfUser) {
		this.levelOfUser = levelOfUser;
	}


	public Date getTimeUserEntersDungeon() {
		return timeUserEntersDungeon;
	}


	public void setTimeUserEntersDungeon(Date timeUserEntersDungeon) {
		this.timeUserEntersDungeon = timeUserEntersDungeon;
	}


	@Override
	public String toString() {
		return "PreDungeonUserInfo [id=" + id + ", userId=" + userId
				+ ", health=" + health + ", mana=" + mana + ", combatRoomId="
				+ combatRoomId + ", levelOfUser=" + levelOfUser
				+ ", timeUserEntersDungeon=" + timeUserEntersDungeon + "]";
	}

	
	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
}
