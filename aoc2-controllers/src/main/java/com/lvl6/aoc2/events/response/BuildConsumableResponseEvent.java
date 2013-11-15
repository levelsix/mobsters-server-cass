package com.lvl6.aoc2.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.aoc2.events.NormalResponseEvent;
import com.lvl6.aoc2.eventprotos.BuildConsumableEventProto.BuildConsumableResponseProto;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;


public class BuildConsumableResponseEvent extends NormalResponseEvent {

  private BuildConsumableResponseProto buildConsumableResponseProto;
  
  public BuildConsumableResponseEvent(String playerId) {
    super(playerId);
    eventType = AocTwoEventProtocolResponse.S_REFILL_HP_OR_MANA_WITH_CONSUMABLE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = buildConsumableResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setBuildConsumableResponseProto(BuildConsumableResponseProto buildConsumableResponseProto) {
    this.buildConsumableResponseProto = buildConsumableResponseProto;
  }
  
}