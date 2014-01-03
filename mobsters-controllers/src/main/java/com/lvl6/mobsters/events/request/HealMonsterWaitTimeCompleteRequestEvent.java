package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterWaitTimeCompleteRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class HealMonsterWaitTimeCompleteRequestEvent extends RequestEvent {
  
  private HealMonsterWaitTimeCompleteRequestProto healMonsterWaitTimeCompleteRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      healMonsterWaitTimeCompleteRequestProto = HealMonsterWaitTimeCompleteRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = healMonsterWaitTimeCompleteRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public HealMonsterWaitTimeCompleteRequestProto getHealMonsterWaitTimeCompleteRequestProto() {
    return healMonsterWaitTimeCompleteRequestProto;
  }
  
}