package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventUserProto.UpdateClientUserResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class UpdateClientUserResponseEvent extends NormalResponseEvent {

  private UpdateClientUserResponseProto updateClientUserResponseProto;
  
  public UpdateClientUserResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_QUEST_ACCEPT_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = updateClientUserResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setUpdateClientUserResponseProto(UpdateClientUserResponseProto updateClientUserResponseProto) {
    this.updateClientUserResponseProto = updateClientUserResponseProto;
  }
  
}