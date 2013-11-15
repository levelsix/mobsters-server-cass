package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.SpeedUpRepairEquipEventProto.SpeedUpRepairEquipRequestProto;

public class SpeedUpRepairEquipRequestEvent extends RequestEvent {
  
  private SpeedUpRepairEquipRequestProto speedUpRepairEquipRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      speedUpRepairEquipRequestProto = SpeedUpRepairEquipRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = speedUpRepairEquipRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public SpeedUpRepairEquipRequestProto getSpeedUpRepairEquipRequestProto() {
    return speedUpRepairEquipRequestProto;
  }
  
}