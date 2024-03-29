package com.lvl6.mobsters.events;

import java.nio.ByteBuffer;

/**
 * GameEventDefault.java
 *
 * A basic GameEvent class, this can be extended for other Events
 * or a completely different class may be used as required by a specific game.
 */

public abstract class RequestEvent extends GameEvent{

	public abstract void read (ByteBuffer bb);

	protected String playerId;   //refers to whoever sent the event/triggered it
	protected int tag;

	public String getPlayerId() {
		return playerId;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

}// GameEvent
