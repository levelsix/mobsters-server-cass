package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventBoosterPackProto.PurchaseBoosterPackResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class PurchaseBoosterPackResponseEvent extends NormalResponseEvent {

  private PurchaseBoosterPackResponseProto purchaseBoosterPackResponseProto;
  
  public PurchaseBoosterPackResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_PURCHASE_BOOSTER_PACK_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = purchaseBoosterPackResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setPurchaseBoosterPackResponseProto(PurchaseBoosterPackResponseProto purchaseBoosterPackResponseProto) {
    this.purchaseBoosterPackResponseProto = purchaseBoosterPackResponseProto;
  }
  
}