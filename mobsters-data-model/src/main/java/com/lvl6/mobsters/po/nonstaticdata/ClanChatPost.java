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
public class ClanChatPost extends BasePersistentObject<UUID> implements Serializable {

	private static final long serialVersionUID = 2285466995081010376L;

	@Id
	protected UUID id = UUID.randomUUID();
	
	@Column(name="poster_id")
	@Index
	protected UUID posterId = null;
	
	@Column(name="clan_id")
	@Index
	protected UUID clanId = null;
	
	@Column(name="time_of_post")
	@Index
	protected Date timeOfPost = null;
	
	@Column(name="content")
	@Index
	protected String content = "";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getPosterId() {
		return posterId;
	}

	public void setPosterId(UUID posterId) {
		this.posterId = posterId;
	}

	public UUID getClanId() {
		return clanId;
	}

	public void setClanId(UUID clanId) {
		this.clanId = clanId;
	}

	public Date getTimeOfPost() {
		return timeOfPost;
	}

	public void setTimeOfPost(Date timeOfPost) {
		this.timeOfPost = timeOfPost;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ClanChatPost [id=" + id + ", posterId=" + posterId
				+ ", clanId=" + clanId + ", timeOfPost=" + timeOfPost
				+ ", content=" + content + "]";
	}

}
