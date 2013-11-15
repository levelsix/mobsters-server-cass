package com.lvl6.aoc2.po;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.lvl6.aoc2.entitymanager.Index;



@Entity
public class UserDevice extends BasePersistentObject{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="udid")
	@Index
	protected String udid = null;

	@Column(name="device_id")
	protected String deviceId = null;
	
	@Column(name="date_linked")
	protected Date dateLinked = new Date();
	
	@Column(name="last_login")
	@Index
	protected Date lastLogin = null;

	@Column(name="last_logout")
	@Index
	protected Date lastLogout = null;



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


	public String getUdid() {
		return udid;
	}


	public void setUdid(String udid) {
		this.udid = udid;
	}


	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


	public Date getDateLinked() {
		return dateLinked;
	}


	public void setDateLinked(Date dateLinked) {
		this.dateLinked = dateLinked;
	}


	public Date getLastLogin() {
		return lastLogin;
	}


	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}


	public Date getLastLogout() {
		return lastLogout;
	}


	public void setLastLogout(Date lastLogout) {
		this.lastLogout = lastLogout;
	}


	@Override
	public String toString() {
		return "UserDevice [id=" + id + ", userId=" + userId + ", udid=" + udid
				+ ", deviceId=" + deviceId + ", dateLinked=" + dateLinked
				+ ", lastLogin=" + lastLogin + ", lastLogout=" + lastLogout
				+ "]";
	}


	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
	
}
