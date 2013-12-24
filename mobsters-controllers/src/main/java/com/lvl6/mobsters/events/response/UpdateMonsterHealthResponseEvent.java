package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.UpdateMonsterHealthResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class UpdateMonsterHealthResponseEvent extends NormalResponseEvent {

  private UpdateMonsterHealthResponseProto updateMonsterHealthResponseProto;
  
  public UpdateMonsterHealthResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_UPDATE_MONSTER_HEALTH_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = updateMonsterHealthResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setUpdateMonsterHealthResponseProto(UpdateMonsterHealthResponseProto updateMonsterHealthResponseProto) {
    this.updateMonsterHealthResponseProto = updateMonsterHealthResponseProto;
  }
  
}