package com.lvl6.mobsters.po.nonstaticdata;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class ExpansionPurchaseForUser extends BasePersistentObject<UUID> implements Serializable {

	private static final long serialVersionUID = -1324119879296803843L;

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="x_position")
	@Index
	protected int xPosition = 0;
	
	@Column(name="y_position")
	@Index
	protected int yPosition = 0;
	
	@Column(name="is_expanding")
	protected boolean isExpanding = false;
	
	@Column(name="expand_start_time")
	protected Date expandStartTime = null;

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

	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public boolean isExpanding() {
		return isExpanding;
	}

	public void setExpanding(boolean isExpanding) {
		this.isExpanding = isExpanding;
	}

	public Date getExpandStartTime() {
		return expandStartTime;
	}

	public void setExpandStartTime(Date expandStartTime) {
		this.expandStartTime = expandStartTime;
	}

	@Override
	public String toString() {
		return "ExpansionPurchaseForUser [id=" + id + ", userId=" + userId
				+ ", xPosition=" + xPosition + ", yPosition=" + yPosition
				+ ", isExpanding=" + isExpanding + ", expandStartTime="
				+ expandStartTime + "]";
	}
	
}
