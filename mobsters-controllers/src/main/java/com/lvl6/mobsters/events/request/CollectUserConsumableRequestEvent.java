package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.lvl6.mobsters.events.RequestEvent;

public class CollectUserConsumableRequestEvent extends RequestEvent {
  
//  private CollectUserConsumableRequestProto collectUserConsumableRequestProto;

  @Override
  public void read(ByteBuffer bb) {
//    try {
//      collectUserConsumableRequestProto = CollectUserConsumableRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = collectUserConsumableRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
  }
  
//  public CollectUserConsumableRequestProto getCollectUserConsumableRequestProto() {
//    return collectUserConsumableRequestProto;
//  }
  
}