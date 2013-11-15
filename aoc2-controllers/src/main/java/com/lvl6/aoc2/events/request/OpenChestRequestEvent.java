package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.OpenChestEventProto.OpenChestRequestProto;

public class OpenChestRequestEvent extends RequestEvent {
  
  private OpenChestRequestProto openChestRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      openChestRequestProto = OpenChestRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = openChestRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public OpenChestRequestProto getOpenChestRequestProto() {
    return openChestRequestProto;
  }
  
}