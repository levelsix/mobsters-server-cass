package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadPlayerCityRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class LoadPlayerCityRequestEvent extends RequestEvent {
  
  private LoadPlayerCityRequestProto loadPlayerCityRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      loadPlayerCityRequestProto = LoadPlayerCityRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = loadPlayerCityRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public LoadPlayerCityRequestProto getLoadPlayerCityRequestProto() {
    return loadPlayerCityRequestProto;
  }
  
}