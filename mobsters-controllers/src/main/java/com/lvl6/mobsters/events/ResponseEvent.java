package com.lvl6.mobsters.events;

import java.nio.ByteBuffer;

/**
 * GameEventDefault.java
 *
 * A basic GameEvent class, this can be extended for other Events
 * or a completely different class may be used as required by a specific game.
 */

public abstract class ResponseEvent extends GameEvent{
  
  /** event type */
  protected int eventType;
	protected int tag;
     
  public int getEventType() {
    return eventType;
  }
    
  public abstract int write (ByteBuffer bb);

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

//	@Override
//	public String toString() {
//		return ReflectionToStringBuilder.toString(this);
//	}
}// GameEvent
