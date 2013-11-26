package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventStructureProto.PurchaseNormStructureRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class PurchaseNormStructureRequestEvent extends RequestEvent {
  
  private PurchaseNormStructureRequestProto purchaseNormStructureRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      purchaseNormStructureRequestProto = PurchaseNormStructureRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = purchaseNormStructureRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public PurchaseNormStructureRequestProto getPurchaseNormStructureRequestProto() {
    return purchaseNormStructureRequestProto;
  }
  
}