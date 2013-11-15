package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.PurchaseGoldOrTonicEventProto.PurchaseGoldOrTonicRequestProto;

public class PurchaseGoldOrTonicRequestEvent extends RequestEvent {
  
  private PurchaseGoldOrTonicRequestProto purchaseGoldOrTonicRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      purchaseGoldOrTonicRequestProto = PurchaseGoldOrTonicRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = purchaseGoldOrTonicRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public PurchaseGoldOrTonicRequestProto getPurchaseGoldOrTonicRequestProto() {
    return purchaseGoldOrTonicRequestProto;
  }
  
}