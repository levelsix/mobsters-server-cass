package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class UserBanned extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = -5784241556874575152L;

	@Id
	protected Integer id = 0;
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="time_banned")
	@Index
	protected Date timeBanned = new Date();

	@Column(name="reason")
	protected String reason = "";
		

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public Date getTimeBanned() {
		return timeBanned;
	}

	public void setTimeBanned(Date timeBanned) {
		this.timeBanned = timeBanned;
	}

	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "UserBanned [id=" + id + ", userId=" + userId + ", timeBanned="
				+ timeBanned + ", reason=" + reason + "]";
	}

}
