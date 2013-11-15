package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.CollectUserEquipEventProto.CollectUserEquipRequestProto;

public class CollectUserEquipRequestEvent extends RequestEvent {
  
  private CollectUserEquipRequestProto collectUserEquipRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      collectUserEquipRequestProto = CollectUserEquipRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = collectUserEquipRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public CollectUserEquipRequestProto getCollectUserEquipRequestProto() {
    return collectUserEquipRequestProto;
  }
  
}