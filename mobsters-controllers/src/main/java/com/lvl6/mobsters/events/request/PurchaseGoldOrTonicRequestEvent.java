package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.events.RequestEvent;

public class PurchaseGoldOrTonicRequestEvent extends RequestEvent {
  
//  private PurchaseGoldOrTonicRequestProto purchaseGoldOrTonicRequestProto;

  @Override
  public void read(ByteBuffer bb) {
//    try {
//      purchaseGoldOrTonicRequestProto = PurchaseGoldOrTonicRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = purchaseGoldOrTonicRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
  }
  
//  public PurchaseGoldOrTonicRequestProto getPurchaseGoldOrTonicRequestProto() {
//    return purchaseGoldOrTonicRequestProto;
//  }
//  
}