package com.lvl6.aoc2.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.aoc2.events.NormalResponseEvent;
import com.lvl6.aoc2.eventprotos.TrainOrUpgradeSpellEventProto.TrainOrUpgradeSpellResponseProto;
import com.lvl6.aoc2.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;


public class TrainOrUpgradeSpellResponseEvent extends NormalResponseEvent {

  private TrainOrUpgradeSpellResponseProto trainOrUpgradeSpellResponseProto;
  
  public TrainOrUpgradeSpellResponseEvent(String playerId) {
    super(playerId);
    eventType = AocTwoEventProtocolResponse.S_REFILL_HP_OR_MANA_WITH_CONSUMABLE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = trainOrUpgradeSpellResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setTrainOrUpgradeSpellResponseProto(TrainOrUpgradeSpellResponseProto trainOrUpgradeSpellResponseProto) {
    this.trainOrUpgradeSpellResponseProto = trainOrUpgradeSpellResponseProto;
  }
  
}