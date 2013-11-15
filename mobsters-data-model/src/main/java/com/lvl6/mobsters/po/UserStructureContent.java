package com.lvl6.mobsters.po;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.lvl6.mobsters.entitymanager.Index;



@Entity
public class UserStructureContent extends BasePersistentObject{

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="user_structure_id")
	@Index
	protected UUID userStructureId = UUID.randomUUID();
	
	@Column(name="content_type")
	@Index
	protected int contentType = 0;
	
	@Column(name="content_id")
	@Index
	protected UUID contentId = null;

	@Column(name="queue_time")
	protected Date queueTime = new Date();

	@Column(name="start_time")
	@Index
	protected Date startTime = new Date();

	
	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public UUID getUserStructureId() {
		return userStructureId;
	}


	public void setUserStructureId(UUID userStructureId) {
		this.userStructureId = userStructureId;
	}


	public int getContentType() {
		return contentType;
	}


	public void setContentType(int contentType) {
		this.contentType = contentType;
	}


	public UUID getContentId() {
		return contentId;
	}


	public void setContentId(UUID contentId) {
		this.contentId = contentId;
	}


	public Date getQueueTime() {
		return queueTime;
	}


	public void setQueueTime(Date queueTime) {
		this.queueTime = queueTime;
	}


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}



	@Override
	public String toString() {
		return "UserStructureContent [id=" + id + ", userStructureId="
				+ userStructureId + ", contentType=" + contentType
				+ ", contentId=" + contentId + ", queueTime=" + queueTime
				+ ", startTime=" + startTime + "]";
	}


	
	@Override
	public Set<String> getTableUpdateStatements() {
		Set<String> indexes = new HashSet<String>();
		
		return indexes;
	}
	
	
}
