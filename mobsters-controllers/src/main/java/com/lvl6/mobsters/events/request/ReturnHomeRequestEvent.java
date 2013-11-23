//package com.lvl6.mobsters.events.request;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.lvl6.mobsters.eventprotos.ReturnHomeEventProto.ReturnHomeRequestProto;
//import com.lvl6.mobsters.events.RequestEvent;
//
//public class ReturnHomeRequestEvent extends RequestEvent {
//  
//  private ReturnHomeRequestProto returnHomeRequestProto;
//
//  @Override
//  public void read(ByteBuffer bb) {
//    try {
//      returnHomeRequestProto = ReturnHomeRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = returnHomeRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
//  }
//  
//  public ReturnHomeRequestProto getReturnHomeRequestProto() {
//    return returnHomeRequestProto;
//  }
//  
//}