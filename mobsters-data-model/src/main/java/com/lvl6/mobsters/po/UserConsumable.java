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
public class UserConsumable extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="consumable_id")
	@Index
	protected UUID consumableId = null;
	
	@Column(name="quantity")
	protected int quantity = 0;
	
	
	
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

	public UUID getConsumableId() {
		return consumableId;
	}

	public void setConsumableId(UUID consumableId) {
		this.consumableId = consumableId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	
	@Override
	public String toString() {
		return "UserConsumable [id=" + id + ", userId=" + userId
				+ ", consumableId=" + consumableId + ", quantity=" + quantity
				+ "]";
	}

	
	
	
}
