package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.CollectUserConsumableEventProto.CollectUserConsumableRequestProto;

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