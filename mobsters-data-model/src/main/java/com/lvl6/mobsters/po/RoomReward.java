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
public class RoomReward extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="chest_id")
	@Index
	protected UUID chestId = UUID.randomUUID();;
	
	@Column(name="chest_drop_rate")
	@Index
	protected double chestDropRate = 0.0;
	
	@Column(name="equip_id")
	@Index
	protected UUID equipId = UUID.randomUUID();;

	@Column(name="equip_drop_rate")
	protected double equipDropRate = 0.0;



	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public UUID getChestId() {
		return chestId;
	}


	public void setChestId(UUID chestId) {
		this.chestId = chestId;
	}


	public double getChestDropRate() {
		return chestDropRate;
	}


	public void setChestDropRate(double chestDropRate) {
		this.chestDropRate = chestDropRate;
	}


	public UUID getEquipId() {
		return equipId;
	}


	public void setEquipId(UUID equipId) {
		this.equipId = equipId;
	}


	public double getEquipDropRate() {
		return equipDropRate;
	}


	public void setEquipDropRate(double equipDropRate) {
		this.equipDropRate = equipDropRate;
	}

	

	@Override
	public String toString() {
		return "RoomReward [id=" + id + ", chestId=" + chestId
				+ ", chestDropRate=" + chestDropRate + ", equipId=" + equipId
				+ ", equipDropRate=" + equipDropRate + "]";
	}

	
	
	
}
