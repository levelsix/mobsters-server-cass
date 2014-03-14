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
public class ClanForUser extends BasePersistentObject<UUID> implements Serializable {

	private static final long serialVersionUID = -4721469970094420546L;

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="clan_id")
	@Index
	protected UUID clanId = null;
	
	//clan member status: requesting, member, captain... (Clan.proto)
	@Column(name="status")
	@Index
	protected String status = "";
	
	@Column(name="time_of_entry")
	@Index
	protected Date timeOfEntry = null;

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

	public UUID getClanId() {
		return clanId;
	}

	public void setClanId(UUID clanId) {
		this.clanId = clanId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	@Override
	public String toString() {
		return "ClanForUser [id=" + id + ", userId=" + userId + ", clanId="
				+ clanId + ", status=" + status + ", timeOfEntry="
				+ timeOfEntry + "]";
	}
	
}
