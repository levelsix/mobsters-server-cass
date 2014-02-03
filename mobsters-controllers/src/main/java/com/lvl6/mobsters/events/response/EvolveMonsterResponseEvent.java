package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolveMonsterResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class EvolveMonsterResponseEvent extends NormalResponseEvent {

  private EvolveMonsterResponseProto evolveMonsterResponseProto;
  
  public EvolveMonsterResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_EVOLVE_MONSTER_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = evolveMonsterResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setEvolveMonsterResponseProto(EvolveMonsterResponseProto evolveMonsterResponseProto) {
    this.evolveMonsterResponseProto = evolveMonsterResponseProto;
  }
  
}