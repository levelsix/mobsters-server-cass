package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class IncreaseMonsterInventorySlotRequestEvent extends RequestEvent {
  
  private IncreaseMonsterInventorySlotRequestProto increaseMonsterInventorySlotRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      increaseMonsterInventorySlotRequestProto = IncreaseMonsterInventorySlotRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = increaseMonsterInventorySlotRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public IncreaseMonsterInventorySlotRequestProto getIncreaseMonsterInventorySlotRequestProto() {
    return increaseMonsterInventorySlotRequestProto;
  }
  
}