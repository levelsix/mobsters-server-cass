package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.EndDungeonResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class EndDungeonResponseEvent extends NormalResponseEvent {

  private EndDungeonResponseProto endDungeonResponseProto;
  
  public EndDungeonResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_END_DUNGEON_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = endDungeonResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setEndDungeonResponseProto(EndDungeonResponseProto endDungeonResponseProto) {
    this.endDungeonResponseProto = endDungeonResponseProto;
  }
  
}