package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.SpeedUpBuildUserConsumableEventProto.SpeedUpBuildUserConsumableRequestProto;

public class SpeedUpBuildUserConsumableRequestEvent extends RequestEvent {
  
  private SpeedUpBuildUserConsumableRequestProto speedUpBuildUserConsumableRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      speedUpBuildUserConsumableRequestProto = SpeedUpBuildUserConsumableRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = speedUpBuildUserConsumableRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public SpeedUpBuildUserConsumableRequestProto getSpeedUpBuildUserConsumableRequestProto() {
    return speedUpBuildUserConsumableRequestProto;
  }
  
} 