package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.SpeedUpBuildOrUpgradeStructureEventProto.SpeedUpBuildOrUpgradeStructureRequestProto;

public class SpeedUpBuildOrUpgradeStructureRequestEvent extends RequestEvent {
  
  private SpeedUpBuildOrUpgradeStructureRequestProto speedUpBuildOrUpgradeStructureRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      speedUpBuildOrUpgradeStructureRequestProto = SpeedUpBuildOrUpgradeStructureRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = speedUpBuildOrUpgradeStructureRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public SpeedUpBuildOrUpgradeStructureRequestProto getSpeedUpBuildOrUpgradeStructureRequestProto() {
    return speedUpBuildOrUpgradeStructureRequestProto;
  }
  
}