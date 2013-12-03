package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventStructureProto.UpgradeNormStructureRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class UpgradeNormStructureRequestEvent extends RequestEvent {
  
  private UpgradeNormStructureRequestProto upgradeNormStructureRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      upgradeNormStructureRequestProto = UpgradeNormStructureRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = upgradeNormStructureRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public UpgradeNormStructureRequestProto getUpgradeNormStructureRequestProto() {
    return upgradeNormStructureRequestProto;
  }
  
}