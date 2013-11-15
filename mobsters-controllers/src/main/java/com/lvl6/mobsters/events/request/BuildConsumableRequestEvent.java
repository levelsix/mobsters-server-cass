package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.BuildConsumableEventProto.BuildConsumableRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class BuildConsumableRequestEvent extends RequestEvent {
  
  private BuildConsumableRequestProto buildConsumableRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      buildConsumableRequestProto = BuildConsumableRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = buildConsumableRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public BuildConsumableRequestProto getBuildConsumableRequestProto() {
    return buildConsumableRequestProto;
  }
  
}