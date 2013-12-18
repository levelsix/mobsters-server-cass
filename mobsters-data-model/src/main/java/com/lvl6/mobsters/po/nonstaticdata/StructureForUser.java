package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;
import com.lvl6.mobsters.utils.CoordinatePair;



@Entity
public class StructureForUser extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = UUID.randomUUID();
	
	//refers to structure table row key
	@Column(name="structure_id")
	@Index
	protected int structureId = 0;
	
	@Column(name="last_collect_time")
	@Index
	protected Date lastCollectTime = new Date();
	
	@Column(name="x_coordinate")
	protected float xCoordinate = 0;
	
	@Column(name="y_coordinate")
	protected float yCoordinate = 0;

	@Column(name="purchase_time")
	@Index
	protected Date purchaseTime = new Date();

	@Column(name="is_complete")
	@Index
	protected boolean isComplete = false;

	@Column(name="struct_orientation")
	protected String structOrientation = "";
	
	@Column(name="fb_invite_struct_lvl")
	protected int fbInviteStructLvl;
	
	
	//convenience methods
	public CoordinatePair getCoordinates() {
		return new CoordinatePair(getxCoordinate(), getyCoordinate());
	}

	
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

	public int getStructureId() {
		return structureId;
	}

	public void setStructureId(int structureId) {
		this.structureId = structureId;
	}

	public Date getLastCollectTime() {
		return lastCollectTime;
	}

	public void setLastCollectTime(Date lastCollectTime) {
		this.lastCollectTime = lastCollectTime;
	}

	public float getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(float xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public float getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(float yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	public Date getPurchaseTime() {
		return purchaseTime;
	}

	public void setPurchaseTime(Date purchaseTime) {
		this.purchaseTime = purchaseTime;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public String getStructOrientation() {
		return structOrientation;
	}

	public void setStructOrientation(String structOrientation) {
		this.structOrientation = structOrientation;
	}

	public int getFbInviteStructLvl() {
		return fbInviteStructLvl;
	}

	public void setFbInviteStructLvl(int fbInviteStructLvl) {
		this.fbInviteStructLvl = fbInviteStructLvl;
	}

	@Override
	public String toString() {
		return "StructureForUser [id=" + id + ", userId=" + userId
				+ ", structureId=" + structureId + ", lastCollectTime="
				+ lastCollectTime + ", xCoordinate=" + xCoordinate
				+ ", yCoordinate=" + yCoordinate + ", purchaseTime="
				+ purchaseTime + ", isComplete=" + isComplete
				+ ", structOrientation=" + structOrientation
				+ ", fbInviteStructLvl=" + fbInviteStructLvl + "]";
	}

}
