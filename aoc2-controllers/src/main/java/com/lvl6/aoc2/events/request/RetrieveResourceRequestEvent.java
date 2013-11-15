package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.RetrieveResourceEventProto.RetrieveResourceRequestProto;

public class RetrieveResourceRequestEvent extends RequestEvent {
  
  private RetrieveResourceRequestProto retrieveResourceRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      retrieveResourceRequestProto = RetrieveResourceRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = retrieveResourceRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public RetrieveResourceRequestProto getRetrieveResourceRequestProto() {
    return retrieveResourceRequestProto;
  }
  
}