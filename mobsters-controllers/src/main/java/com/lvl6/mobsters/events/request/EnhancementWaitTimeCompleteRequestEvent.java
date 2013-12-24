package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EnhancementWaitTimeCompleteRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class EnhancementWaitTimeCompleteRequestEvent extends RequestEvent {
  
  private EnhancementWaitTimeCompleteRequestProto enhancementWaitTimeCompleteRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      enhancementWaitTimeCompleteRequestProto = EnhancementWaitTimeCompleteRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = enhancementWaitTimeCompleteRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public EnhancementWaitTimeCompleteRequestProto getEnhancementWaitTimeCompleteRequestProto() {
    return enhancementWaitTimeCompleteRequestProto;
  }
  
}