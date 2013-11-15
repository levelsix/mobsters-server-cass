package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.RepairEquipWhenMissingResourcesEventProto.RepairEquipWhenMissingResourcesRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class RepairEquipWhenMissingResourcesRequestEvent extends RequestEvent {
  
  private RepairEquipWhenMissingResourcesRequestProto repairEquipWhenMissingResourcesRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      repairEquipWhenMissingResourcesRequestProto = RepairEquipWhenMissingResourcesRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = repairEquipWhenMissingResourcesRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public RepairEquipWhenMissingResourcesRequestProto getRepairEquipWhenMissingResourcesRequestProto() {
    return repairEquipWhenMissingResourcesRequestProto;
  }
  
}