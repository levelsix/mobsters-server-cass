package com.lvl6.aoc2.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Player.java
 * 
 * Basic Player information
 */
public class ConnectedPlayer implements Serializable {

	private static final long serialVersionUID = -4695628631220580445L;
	
	protected String playerId;
	protected String udid = "";
	protected Date lastMessageSentToServer = new Date();

	
	public Date getLastMessageSentToServer() {
		return lastMessageSentToServer;
	}

	public void setLastMessageSentToServer(Date lastMessageSentToServer) {
		this.lastMessageSentToServer = lastMessageSentToServer;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String id) {
		playerId = id;
	}

}
