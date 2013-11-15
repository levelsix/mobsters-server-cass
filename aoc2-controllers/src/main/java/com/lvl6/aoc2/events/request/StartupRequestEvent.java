package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.PreDatabaseRequestEvent;
import com.lvl6.aoc2.eventprotos.StartupEventProto.StartupRequestProto;

public class StartupRequestEvent extends PreDatabaseRequestEvent {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private StartupRequestProto startupRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      startupRequestProto = StartupRequestProto.parseFrom(ByteString.copyFrom(buff));
      
      //Player id is "" since it won't be initialized yet.
      playerId = "";//startupRequestProto.getSender().getUserId();
      
      udid = startupRequestProto.getMup().getUdid();
    } catch (InvalidProtocolBufferException e) {
      log.error("unexpected error: StartupRequestEvent. ", e);
    }
  }

  public StartupRequestProto getStartupRequestProto() {
    return startupRequestProto;
  }

  public void setStartupRequestProto(StartupRequestProto startupRequestProto) {
      this.startupRequestProto = startupRequestProto;
  }
}
