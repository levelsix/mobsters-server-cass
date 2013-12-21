package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SubmitMonsterEnhancementRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class SubmitMonsterEnhancementRequestEvent extends RequestEvent {
  
  private SubmitMonsterEnhancementRequestProto submitMonsterEnhancementRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      submitMonsterEnhancementRequestProto = SubmitMonsterEnhancementRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = submitMonsterEnhancementRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public SubmitMonsterEnhancementRequestProto getSubmitMonsterEnhancementRequestProto() {
    return submitMonsterEnhancementRequestProto;
  }
  
}