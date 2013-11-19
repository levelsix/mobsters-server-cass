package com.lvl6.mobsters.po;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class MonsterAndRoom extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="monster_id")
	@Index
	protected UUID monsterId = UUID.randomUUID();;
	
	@Column(name="combat_room_id")
	@Index
	protected String combatRoomId = "";
	
	@Column(name="quantity")
	protected int quantity = 0;
	
	//where monster spawns in a particular map of a combat room
	@Column(name="spawn_point")
	protected int spawnPoint = 0;




	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public UUID getMonsterId() {
		return monsterId;
	}


	public void setMonsterId(UUID monsterId) {
		this.monsterId = monsterId;
	}


	public String getCombatRoomId() {
		return combatRoomId;
	}


	public void setCombatRoomId(String combatRoomId) {
		this.combatRoomId = combatRoomId;
	}


	public int getQuantity() {
		return quantity;
	}


	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


	public int getSpawnPoint() {
		return spawnPoint;
	}


	public void setSpawnPoint(int spawnPoint) {
		this.spawnPoint = spawnPoint;
	}


	@Override
	public String toString() {
		return "MonsterAndRoom [id=" + id + ", monsterId=" + monsterId
				+ ", combatRoomId=" + combatRoomId + ", quantity=" + quantity
				+ ", spawnPoint=" + spawnPoint + "]";
	}


	
}
