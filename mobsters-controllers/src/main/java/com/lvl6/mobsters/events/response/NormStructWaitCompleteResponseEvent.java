package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventStructureProto.NormStructWaitCompleteResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class NormStructWaitCompleteResponseEvent extends NormalResponseEvent {

  private NormStructWaitCompleteResponseProto normStructWaitCompleteResponseProto;
  
  public NormStructWaitCompleteResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_UPGRADE_NORM_STRUCTURE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = normStructWaitCompleteResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setNormStructWaitCompleteResponseProto(NormStructWaitCompleteResponseProto normStructWaitCompleteResponseProto) {
    this.normStructWaitCompleteResponseProto = normStructWaitCompleteResponseProto;
  }
  
}