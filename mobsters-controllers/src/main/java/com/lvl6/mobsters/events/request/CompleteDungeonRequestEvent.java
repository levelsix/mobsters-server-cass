package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.CompleteDungeonEventProto.CompleteDungeonRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class CompleteDungeonRequestEvent extends RequestEvent {
  
  private CompleteDungeonRequestProto completeDungeonRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      completeDungeonRequestProto = CompleteDungeonRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = completeDungeonRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public CompleteDungeonRequestProto getCompleteDungeonRequestProto() {
    return completeDungeonRequestProto;
  }
  
}