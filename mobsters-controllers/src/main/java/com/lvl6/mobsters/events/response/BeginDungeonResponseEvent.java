package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.BeginDungeonResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class BeginDungeonResponseEvent extends NormalResponseEvent {

  private BeginDungeonResponseProto beginDungeonResponseProto;
  
  public BeginDungeonResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_BEGIN_DUNGEON_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = beginDungeonResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setBeginDungeonResponseProto(BeginDungeonResponseProto beginDungeonResponseProto) {
    this.beginDungeonResponseProto = beginDungeonResponseProto;
  }
  
}