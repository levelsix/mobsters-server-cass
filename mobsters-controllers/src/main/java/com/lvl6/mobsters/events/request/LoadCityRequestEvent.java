package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadCityRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class LoadCityRequestEvent extends RequestEvent {
  
  private LoadCityRequestProto loadCityRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      loadCityRequestProto = LoadCityRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = loadCityRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public LoadCityRequestProto getLoadCityRequestProto() {
    return loadCityRequestProto;
  }
  
}