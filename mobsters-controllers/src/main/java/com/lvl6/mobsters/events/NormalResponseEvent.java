package com.lvl6.mobsters.events;

public abstract class NormalResponseEvent extends ResponseEvent{
  protected String playerId;   //refers to whoever sent the event/triggered it

  public String getPlayerId() {
    return playerId;
  }
  
  public NormalResponseEvent(String playerId) {
    this.playerId = playerId;
  }
}
