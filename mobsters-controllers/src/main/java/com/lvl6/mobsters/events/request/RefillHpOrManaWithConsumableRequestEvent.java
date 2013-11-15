package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.RefillHpOrManaWithConsumableEventProto.RefillHpOrManaWithConsumableRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

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