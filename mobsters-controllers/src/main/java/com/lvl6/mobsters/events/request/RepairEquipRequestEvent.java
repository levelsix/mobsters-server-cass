//package com.lvl6.mobsters.events.request;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.lvl6.mobsters.eventprotos.RepairEquipEventProto.RepairEquipRequestProto;
//import com.lvl6.mobsters.events.RequestEvent;
//
//public class RepairEquipRequestEvent extends RequestEvent {
//  
//  private RepairEquipRequestProto repairEquipRequestProto;
//
//  @Override
//  public void read(ByteBuffer bb) {
//    try {
//      repairEquipRequestProto = RepairEquipRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = repairEquipRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
//  }
//  
//  public RepairEquipRequestProto getRepairEquipRequestProto() {
//    return repairEquipRequestProto;
//  }
//  
//}