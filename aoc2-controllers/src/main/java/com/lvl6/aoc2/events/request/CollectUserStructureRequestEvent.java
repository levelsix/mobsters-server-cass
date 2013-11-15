package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.CollectUserStructureEventProto.CollectUserStructureRequestProto;

public class CollectUserStructureRequestEvent extends RequestEvent {
  
  private CollectUserStructureRequestProto collectUserStructureRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      collectUserStructureRequestProto = CollectUserStructureRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = collectUserStructureRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public CollectUserStructureRequestProto getCollectUserStructureRequestProto() {
    return collectUserStructureRequestProto;
  }
  
}