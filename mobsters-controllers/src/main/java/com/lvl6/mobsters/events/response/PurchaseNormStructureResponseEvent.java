package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventStructureProto.PurchaseNormStructureResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class PurchaseNormStructureResponseEvent extends NormalResponseEvent {

  private PurchaseNormStructureResponseProto purchaseNormStructureResponseProto;
  
  public PurchaseNormStructureResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_PURCHASE_NORM_STRUCTURE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = purchaseNormStructureResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setPurchaseNormStructureResponseProto(PurchaseNormStructureResponseProto purchaseNormStructureResponseProto) {
    this.purchaseNormStructureResponseProto = purchaseNormStructureResponseProto;
  }
  
}