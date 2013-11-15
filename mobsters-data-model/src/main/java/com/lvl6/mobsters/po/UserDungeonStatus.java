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
public class UserDungeonStatus extends BasePersistentObject{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="combat_room_id")
	@Index
	protected String combatRoomId = "";
	
	//the 'equip_id' column in equipment table, not the 'id' column
	@Column(name="hp")
	protected int hp = 0;
	
	@Column(name="mana")
	protected int mana = 0;
	
	@Column(name="actions_performed")
	protected int actionsPerformed = 0;
	
	@Column(name="current_time")
	@Index
	protected Date currentTime = new Date();



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

	public String getCombatRoomId() {
		return combatRoomId;
	}

	public void setCombatRoomId(String combatRoomId) {
		this.combatRoomId = combatRoomId;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getActionsPerformed() {
		return actionsPerformed;
	}

	public void setActionsPerformed(int actionsPerformed) {
		this.actionsPerformed = actionsPerformed;
	}

	public Date getCurrentTime() {
		return currentTime;
	}
	
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}




	@Override
	public String toString() {
		return "UserDungeonStatus [id=" + id + ", userId=" + userId
				+ ", combatRoomId=" + combatRoomId + ", hp=" + hp + ", mana="
				+ mana + ", actionsPerformed=" + actionsPerformed
				+ ", currentTime=" + currentTime + "]";
	}



	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
	
}
