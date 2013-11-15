package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.CollectUserConsumableEventProto.CollectUserConsumableRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class CollectUserConsumableRequestEvent extends RequestEvent {
  
  private CollectUserConsumableRequestProto collectUserConsumableRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      collectUserConsumableRequestProto = CollectUserConsumableRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = collectUserConsumableRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public CollectUserConsumableRequestProto getCollectUserConsumableRequestProto() {
    return collectUserConsumableRequestProto;
  }
  
}