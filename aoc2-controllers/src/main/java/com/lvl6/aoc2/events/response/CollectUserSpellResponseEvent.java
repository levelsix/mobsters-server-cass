package com.lvl6.aoc2.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.aoc2.events.NormalResponseEvent;
import com.lvl6.aoc2.eventprotos.CollectUserSpellEventProto.CollectUserSpellResponseProto;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;


public class CollectUserSpellResponseEvent extends NormalResponseEvent {

  private CollectUserSpellResponseProto collectUserSpellResponseProto;
  
  public CollectUserSpellResponseEvent(String playerId) {
    super(playerId);
    eventType = AocTwoEventProtocolResponse.S_REFILL_HP_OR_MANA_WITH_CONSUMABLE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = collectUserSpellResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setCollectUserSpellResponseProto(CollectUserSpellResponseProto collectUserSpellResponseProto) {
    this.collectUserSpellResponseProto = collectUserSpellResponseProto;
  }
  
}