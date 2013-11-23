package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.lvl6.mobsters.events.RequestEvent;

public class CollectUserEquipRequestEvent extends RequestEvent {
  
//  private CollectUserEquipRequestProto collectUserEquipRequestProto;

  @Override
  public void read(ByteBuffer bb) {
//    try {
//      collectUserEquipRequestProto = CollectUserEquipRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = collectUserEquipRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
  }
  
//  public CollectUserEquipRequestProto getCollectUserEquipRequestProto() {
//    return collectUserEquipRequestProto;
//  }
  
}