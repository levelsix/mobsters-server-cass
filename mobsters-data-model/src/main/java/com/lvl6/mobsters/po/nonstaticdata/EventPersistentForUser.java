package com.lvl6.mobsters.po.nonstaticdata;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.dekayd.astyanax.cassandra.entitymanager.BasePersistentObject;
import com.dekayd.astyanax.cassandra.entitymanager.Index;


//row for a user just indicates when the user started an event
//user can have more than one event ongoing (userId, eventPersistentId) are "primary" keys
@Entity
public class EventPersistentForUser extends BasePersistentObject<UUID> implements Serializable {

	private static final long serialVersionUID = 2813443332112168676L;

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_id")
	@Index
	protected UUID userId = null;
	
	@Column(name="event_persistent_id")
	@Index
	protected int eventPersistentId = 0;
	
	@Column(name="time_of_entry")
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

	public int getEventPersistentId() {
		return eventPersistentId;
	}

	public void setEventPersistentId(int eventPersistentId) {
		this.eventPersistentId = eventPersistentId;
	}

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	@Override
	public String toString() {
		return "EventPersistentForUser [id=" + id + ", userId=" + userId
				+ ", eventPersistentId=" + eventPersistentId + ", timeOfEntry="
				+ timeOfEntry + "]";
	}
	
}
