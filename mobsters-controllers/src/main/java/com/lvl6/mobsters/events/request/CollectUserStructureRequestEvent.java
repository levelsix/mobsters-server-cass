package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.lvl6.mobsters.events.RequestEvent;

public class CollectUserStructureRequestEvent extends RequestEvent {
  
  //private CollectUserStructureRequestProto collectUserStructureRequestProto;

  @Override
  public void read(ByteBuffer bb) {
//    try {
//      collectUserStructureRequestProto = CollectUserStructureRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = collectUserStructureRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
  }
  
//  public CollectUserStructureRequestProto getCollectUserStructureRequestProto() {
//    return collectUserStructureRequestProto;
//  }
  
}