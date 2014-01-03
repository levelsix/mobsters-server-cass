package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.HealMonsterResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class HealMonsterResponseEvent extends NormalResponseEvent {

  private HealMonsterResponseProto healMonsterResponseProto;
  
  public HealMonsterResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_HEAL_MONSTER_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = healMonsterResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setHealMonsterResponseProto(HealMonsterResponseProto healMonsterResponseProto) {
    this.healMonsterResponseProto = healMonsterResponseProto;
  }
  
}