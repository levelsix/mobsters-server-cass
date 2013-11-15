package com.lvl6.mobsters.po;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.lvl6.mobsters.entitymanager.Index;



@Entity
public class MonsterNonPersistentDrop extends BasePersistentObject{
	
	//explicit values are useless, I just made random values

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="monster_id")
	@Index
	protected UUID monsterId = UUID.randomUUID();
	
	//monster_event or monster quest or others
	@Column(name="drop_type")
	@Index
	protected int dropType = 0;
	
	//monster-event id or monster-quest id
	@Column(name="drop_type_id")
	@Index
	protected UUID dropTypeId = null;
	
	@Column(name="item_id")
	@Index
	protected UUID itemId = UUID.randomUUID();;
	
	@Column(name="item_drop_rate")
	protected double itemDropRate = 0.5;
	



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


	public int getDropType() {
		return dropType;
	}


	public void setDropType(int dropType) {
		this.dropType = dropType;
	}


	public UUID getDropTypeId() {
		return dropTypeId;
	}


	public void setDropTypeId(UUID dropTypeId) {
		this.dropTypeId = dropTypeId;
	}


	public UUID getItemId() {
		return itemId;
	}


	public void setItemId(UUID itemId) {
		this.itemId = itemId;
	}


	public double getItemDropRate() {
		return itemDropRate;
	}


	public void setItemDropRate(double itemDropRate) {
		this.itemDropRate = itemDropRate;
	}


	@Override
	public String toString() {
		return "MonsterNonPersistentDrop [id=" + id + ", monsterId="
				+ monsterId + ", dropType=" + dropType + ", dropTypeId="
				+ dropTypeId + ", itemId=" + itemId + ", itemDropRate="
				+ itemDropRate + "]";
	}

	
	
	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
}
