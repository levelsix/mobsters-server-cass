package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.RefillHpOrManaWithConsumableEventProto.RefillHpOrManaWithConsumableRequestProto;

public class RefillHpOrManaWithConsumableRequestEvent extends RequestEvent {
  
  private RefillHpOrManaWithConsumableRequestProto refillHpOrManaWithConsumableRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      refillHpOrManaWithConsumableRequestProto = RefillHpOrManaWithConsumableRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = refillHpOrManaWithConsumableRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public RefillHpOrManaWithConsumableRequestProto getRefillHpOrManaWithConsumableRequestProto() {
    return refillHpOrManaWithConsumableRequestProto;
  }
  
}