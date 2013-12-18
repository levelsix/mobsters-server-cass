package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadPlayerCityResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class LoadPlayerCityResponseEvent extends NormalResponseEvent {

  private LoadPlayerCityResponseProto loadPlayerCityResponseProto;
  
  public LoadPlayerCityResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_LOAD_PLAYER_CITY_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = loadPlayerCityResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setLoadPlayerCityResponseProto(LoadPlayerCityResponseProto loadPlayerCityResponseProto) {
    this.loadPlayerCityResponseProto = loadPlayerCityResponseProto;
  }
  
}