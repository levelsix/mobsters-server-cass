package com.lvl6.mobsters.po.staticdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;



@Entity
public class AlertOnStartup extends BasePersistentObject<Integer> implements Serializable{

	private static final long serialVersionUID = -6101951574269517148L;

	@Id
	protected Integer id = 0;
	
	@Column(name="message")
	@Index
	protected String message = "";
	
	@Column(name="is_active")
	protected boolean isActive = false;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "AlertOnStartup [id=" + id + ", message=" + message
				+ ", isActive=" + isActive + "]";
	}

}
