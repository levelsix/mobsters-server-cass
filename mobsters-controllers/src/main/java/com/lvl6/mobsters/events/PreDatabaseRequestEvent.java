package com.lvl6.mobsters.events;

public abstract class PreDatabaseRequestEvent extends RequestEvent{
  protected String udid;

  public String getUdid() {
    return udid;
  }
}
