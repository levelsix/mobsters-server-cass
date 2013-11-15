package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.BuildOrUpgradeStructureEventProto.BuildOrUpgradeStructureRequestProto;

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