//package com.lvl6.mobsters.events.response;
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.lvl6.mobsters.eventprotos.RepairEquipEventProto.RepairEquipResponseProto;
//import com.lvl6.mobsters.events.NormalResponseEvent;
//import com.lvl6.mobsters.noneventprotos.AocTwoEventProtocolProto.AocTwoEventProtocolResponse;
//
//
//public class RepairEquipResponseEvent extends NormalResponseEvent {
//
//  private RepairEquipResponseProto repairEquipResponseProto;
//  
//  public RepairEquipResponseEvent(String playerId) {
//    super(playerId);
//    eventType = AocTwoEventProtocolResponse.S_REPAIR_EQUIP_EVENT_VALUE;
//  }
//  
//  @Override
//  public int write(ByteBuffer bb) {
//    ByteString b = repairEquipResponseProto.toByteString();
//    b.copyTo(bb);
//    return b.size();
//  }
//  
//  public void setRepairEquipResponseProto(RepairEquipResponseProto repairEquipResponseProto) {
//    this.repairEquipResponseProto = repairEquipResponseProto;
//  }
//  
//}