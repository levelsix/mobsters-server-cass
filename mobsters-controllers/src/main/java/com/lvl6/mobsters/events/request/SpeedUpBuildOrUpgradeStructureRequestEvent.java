package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.SpeedUpBuildOrUpgradeStructureEventProto.SpeedUpBuildOrUpgradeStructureRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

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