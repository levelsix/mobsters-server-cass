package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class IncreaseMonsterInventorySlotResponseEvent extends NormalResponseEvent {

  private IncreaseMonsterInventorySlotResponseProto increaseMonsterInventorySlotResponseProto;
  
  public IncreaseMonsterInventorySlotResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_INCREASE_MONSTER_INVENTORY_SLOT_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = increaseMonsterInventorySlotResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setIncreaseMonsterInventorySlotResponseProto(IncreaseMonsterInventorySlotResponseProto increaseMonsterInventorySlotResponseProto) {
    this.increaseMonsterInventorySlotResponseProto = increaseMonsterInventorySlotResponseProto;
  }
  
}