package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.StartDungeonEventProto.StartDungeonRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class StartDungeonRequestEvent extends RequestEvent {
  
  private StartDungeonRequestProto startDungeonRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      startDungeonRequestProto = StartDungeonRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = startDungeonRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public StartDungeonRequestProto getStartDungeonRequestProto() {
    return startDungeonRequestProto;
  }
  
}