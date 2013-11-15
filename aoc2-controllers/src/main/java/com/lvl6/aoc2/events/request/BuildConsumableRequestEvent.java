package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.BuildConsumableEventProto.BuildConsumableRequestProto;

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