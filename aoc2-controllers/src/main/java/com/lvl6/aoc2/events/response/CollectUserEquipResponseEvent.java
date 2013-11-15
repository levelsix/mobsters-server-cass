package com.lvl6.aoc2.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.aoc2.events.NormalResponseEvent;
import com.lvl6.aoc2.eventprotos.CollectUserEquipEventProto.CollectUserEquipResponseProto;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;


public class CollectUserEquipResponseEvent extends NormalResponseEvent {

  private CollectUserEquipResponseProto collectUserEquipResponseProto;
  
  public CollectUserEquipResponseEvent(String playerId) {
    super(playerId);
    eventType = AocTwoEventProtocolResponse.S_REFILL_HP_OR_MANA_WITH_CONSUMABLE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = collectUserEquipResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setCollectUserEquipResponseProto(CollectUserEquipResponseProto collectUserEquipResponseProto) {
    this.collectUserEquipResponseProto = collectUserEquipResponseProto;
  }
  
}