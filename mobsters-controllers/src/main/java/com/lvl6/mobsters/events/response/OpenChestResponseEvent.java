package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.OpenChestEventProto.OpenChestResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;


public class OpenChestResponseEvent extends NormalResponseEvent {

  private OpenChestResponseProto openChestResponseProto;
  
  public OpenChestResponseEvent(String playerId) {
    super(playerId);
    eventType = AocTwoEventProtocolResponse.S_REFILL_HP_OR_MANA_WITH_CONSUMABLE_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = openChestResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setOpenChestResponseProto(OpenChestResponseProto openChestResponseProto) {
    this.openChestResponseProto = openChestResponseProto;
  }
  
}