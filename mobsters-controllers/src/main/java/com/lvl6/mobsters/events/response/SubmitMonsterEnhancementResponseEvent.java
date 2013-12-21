package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.SubmitMonsterEnhancementResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class SubmitMonsterEnhancementResponseEvent extends NormalResponseEvent {

  private SubmitMonsterEnhancementResponseProto submitMonsterEnhancementResponseProto;
  
  public SubmitMonsterEnhancementResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_SUBMIT_MONSTER_ENHANCEMENT_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = submitMonsterEnhancementResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setSubmitMonsterEnhancementResponseProto(SubmitMonsterEnhancementResponseProto submitMonsterEnhancementResponseProto) {
    this.submitMonsterEnhancementResponseProto = submitMonsterEnhancementResponseProto;
  }
  
}