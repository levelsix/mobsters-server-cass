package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.ReturnHomeEventProto.ReturnHomeRequestProto;

public class ReturnHomeRequestEvent extends RequestEvent {
  
  private ReturnHomeRequestProto returnHomeRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      returnHomeRequestProto = ReturnHomeRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = returnHomeRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public ReturnHomeRequestProto getReturnHomeRequestProto() {
    return returnHomeRequestProto;
  }
  
}