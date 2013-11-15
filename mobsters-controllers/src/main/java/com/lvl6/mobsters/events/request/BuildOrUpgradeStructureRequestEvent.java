package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.BuildOrUpgradeStructureEventProto.BuildOrUpgradeStructureRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class BuildOrUpgradeStructureRequestEvent extends RequestEvent {
  
  private BuildOrUpgradeStructureRequestProto buildOrUpgradeStructureRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      buildOrUpgradeStructureRequestProto = BuildOrUpgradeStructureRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = buildOrUpgradeStructureRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public BuildOrUpgradeStructureRequestProto getBuildOrUpgradeStructureRequestProto() {
    return buildOrUpgradeStructureRequestProto;
  }
  
}