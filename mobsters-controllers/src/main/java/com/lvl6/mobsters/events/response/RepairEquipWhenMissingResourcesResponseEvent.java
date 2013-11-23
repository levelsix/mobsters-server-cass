//package com.lvl6.mobsters.events.response;
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.mobsters.eventprotos.RepairEquipWhenMissingResourcesEventProto.RepairEquipWhenMissingResourcesResponseProto;
//import com.lvl6.mobsters.events.NormalResponseEvent;
//import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;
//
//
//public class RepairEquipWhenMissingResourcesResponseEvent extends NormalResponseEvent {
//
//  private RepairEquipWhenMissingResourcesResponseProto repairEquipWhenMissingResourcesResponseProto;
//  
//  public RepairEquipWhenMissingResourcesResponseEvent(String playerId) {
//    super(playerId);
//    eventType = AocTwoEventProtocolResponse.S_REPAIR_EQUIP_EVENT_VALUE;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b = repairEquipWhenMissingResourcesResponseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//  
//  public void setRepairEquipWhenMissingResourcesResponseProto(RepairEquipWhenMissingResourcesResponseProto repairEquipWhenMissingResourcesResponseProto) {
//    this.repairEquipWhenMissingResourcesResponseProto = repairEquipWhenMissingResourcesResponseProto;
//  }
//  
//}