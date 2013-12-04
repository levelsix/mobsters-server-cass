package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class FinishNormStructWaittimeWithDiamondsResponseEvent extends NormalResponseEvent {

  private FinishNormStructWaittimeWithDiamondsResponseProto finishNormStructWaittimeWithDiamondsResponseProto;
  
  public FinishNormStructWaittimeWithDiamondsResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_UPGRADE_NORM_STRUCTURE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = finishNormStructWaittimeWithDiamondsResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setFinishNormStructWaittimeWithDiamondsResponseProto(FinishNormStructWaittimeWithDiamondsResponseProto finishNormStructWaittimeWithDiamondsResponseProto) {
    this.finishNormStructWaittimeWithDiamondsResponseProto = finishNormStructWaittimeWithDiamondsResponseProto;
  }
  
}