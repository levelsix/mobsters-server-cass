package com.lvl6.aoc2.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.aoc2.events.NormalResponseEvent;
import com.lvl6.aoc2.eventprotos.RefillHpOrManaWithConsumableEventProto.RefillHpOrManaWithConsumableResponseProto;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;


public class RefillHpOrManaWithConsumableResponseEvent extends NormalResponseEvent {

  private RefillHpOrManaWithConsumableResponseProto refillHpOrManaWithConsumableResponseProto;
  
  public RefillHpOrManaWithConsumableResponseEvent(String playerId) {
    super(playerId);
    eventType = AocTwoEventProtocolResponse.S_REFILL_HP_OR_MANA_WITH_CONSUMABLE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = refillHpOrManaWithConsumableResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setRefillHpOrManaWithConsumableResponseProto(RefillHpOrManaWithConsumableResponseProto refillHpOrManaWithConsumableResponseProto) {
    this.refillHpOrManaWithConsumableResponseProto = refillHpOrManaWithConsumableResponseProto;
  }
  
}