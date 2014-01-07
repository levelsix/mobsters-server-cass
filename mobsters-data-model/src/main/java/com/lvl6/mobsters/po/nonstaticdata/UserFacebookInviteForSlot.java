package com.lvl6.mobsters.po.nonstaticdata;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class UserFacebookInviteForSlot extends BasePersistentObject<UUID>{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="inviter_user_id")
	@Index
	protected UUID inviterUserId = null;
	
	//facebook id for invite recipient
	@Column(name="recipient_fb_id")
	@Index
	protected String recipientFbId = null;
	
	@Column(name="time_of_invite")
	@Index
	protected Date timeOfInvite = null;

	@Column(name="time_accepted")
	protected Date timeAccepted = null;
	
	@Column(name="user_struct_id")
	protected UUID userStructId = null;
	
	@Column(name="user_struct_fb_lvl")
	protected int userStructFbLvl = 0;
	
	@Column(name="time_redeemed")
	protected Date timeRedeemed = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getInviterUserId() {
		return inviterUserId;
	}

	public void setInviterUserId(UUID inviterUserId) {
		this.inviterUserId = inviterUserId;
	}

	public String getRecipientFbId() {
		return recipientFbId;
	}

	public void setRecipientFbId(String recipientFbId) {
		this.recipientFbId = recipientFbId;
	}

	public Date getTimeOfInvite() {
		return timeOfInvite;
	}

	public void setTimeOfInvite(Date timeOfInvite) {
		this.timeOfInvite = timeOfInvite;
	}

	public Date getTimeAccepted() {
		return timeAccepted;
	}

	public void setTimeAccepted(Date timeAccepted) {
		this.timeAccepted = timeAccepted;
	}

	public UUID getUserStructId() {
		return userStructId;
	}

	public void setUserStructId(UUID userStructId) {
		this.userStructId = userStructId;
	}

	public int getUserStructFbLvl() {
		return userStructFbLvl;
	}

	public void setUserStructFbLvl(int userStructFbLvl) {
		this.userStructFbLvl = userStructFbLvl;
	}

	public Date getTimeRedeemed() {
		return timeRedeemed;
	}

	public void setTimeRedeemed(Date timeRedeemed) {
		this.timeRedeemed = timeRedeemed;
	}

	@Override
	public String toString() {
		return "UserFacebookInviteForSlot [id=" + id + ", inviterUserId="
				+ inviterUserId + ", recipientFbId=" + recipientFbId
				+ ", timeOfInvite=" + timeOfInvite + ", timeAccepted="
				+ timeAccepted + ", userStructId=" + userStructId
				+ ", userStructFbLvl=" + userStructFbLvl + ", timeRedeemed="
				+ timeRedeemed + "]";
	}
	
}
