package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.lvl6.mobsters.events.RequestEvent;

public class CollectUserSpellRequestEvent extends RequestEvent {
  
//  private CollectUserSpellRequestProto collectUserSpellRequestProto;

  @Override
  public void read(ByteBuffer bb) {
//    try {
//      collectUserSpellRequestProto = CollectUserSpellRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = collectUserSpellRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
  }
//  
//  public CollectUserSpellRequestProto getCollectUserSpellRequestProto() {
//    return collectUserSpellRequestProto;
//  }
  
}