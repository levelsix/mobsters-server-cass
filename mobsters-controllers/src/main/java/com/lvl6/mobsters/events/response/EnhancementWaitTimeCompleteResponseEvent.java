package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class EnhancementWaitTimeCompleteResponseEvent extends NormalResponseEvent {

  private EnhancementWaitTimeCompleteResponseProto enhancementWaitTimeCompleteResponseProto;
  
  public EnhancementWaitTimeCompleteResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = enhancementWaitTimeCompleteResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  public void setEnhancementWaitTimeCompleteResponseProto(EnhancementWaitTimeCompleteResponseProto enhancementWaitTimeCompleteResponseProto) {
    this.enhancementWaitTimeCompleteResponseProto = enhancementWaitTimeCompleteResponseProto;
  }
  
}