package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.SpeedUpRepairEquipEventProto.SpeedUpRepairEquipRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

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