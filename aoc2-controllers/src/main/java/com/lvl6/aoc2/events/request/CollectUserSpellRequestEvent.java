package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.CollectUserSpellEventProto.CollectUserSpellRequestProto;

public class CollectUserSpellRequestEvent extends RequestEvent {
  
  private CollectUserSpellRequestProto collectUserSpellRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      collectUserSpellRequestProto = CollectUserSpellRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = collectUserSpellRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public CollectUserSpellRequestProto getCollectUserSpellRequestProto() {
    return collectUserSpellRequestProto;
  }
  
}