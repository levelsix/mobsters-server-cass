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
public class Clan extends BasePersistentObject<UUID> implements Serializable {

	private static final long serialVersionUID = -8713218957644665683L;

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="name")
	@Index //should be unique
	protected String name = "";
	
	@Column(name="create_time")
	@Index
	protected Date createTime = null;
	
	@Column(name="description")
	protected String description = "";
	
	@Column(name="tag")
	@Index //should be unique
	protected String tag = "";
	
	@Column(name="request_to_join_required")
	protected boolean requestToJoinRequired = false;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isRequestToJoinRequired() {
		return requestToJoinRequired;
	}

	public void setRequestToJoinRequired(boolean requestToJoinRequired) {
		this.requestToJoinRequired = requestToJoinRequired;
	}

	@Override
	public String toString() {
		return "Clan [id=" + id + ", name=" + name + ", createTime="
				+ createTime + ", description=" + description + ", tag=" + tag
				+ ", requestToJoinRequired=" + requestToJoinRequired + "]";
	}
	
}
