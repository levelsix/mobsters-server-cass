package com.lvl6.mobsters.events.response;
import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadCityResponseProto;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolResponse;


public class LoadCityResponseEvent extends NormalResponseEvent {

  private LoadCityResponseProto loadCityResponseProto;
  
  public LoadCityResponseEvent(String playerId) {
    super(playerId);
    eventType = MobstersEventProtocolResponse.S_LOAD_CITY_EVENT_VALUE;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = loadCityResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }
  
  public void setLoadCityResponseProto(LoadCityResponseProto loadCityResponseProto) {
    this.loadCityResponseProto = loadCityResponseProto;
  }
  
}