package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.lvl6.mobsters.events.RequestEvent;

public class CompleteDungeonRequestEvent extends RequestEvent {
  
//  private CompleteDungeonRequestProto completeDungeonRequestProto;

  @Override
  public void read(ByteBuffer bb) {
//    try {
//      completeDungeonRequestProto = CompleteDungeonRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = completeDungeonRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
  }
//  
//  public CompleteDungeonRequestProto getCompleteDungeonRequestProto() {
//    return completeDungeonRequestProto;
//  }
  
}