//package com.lvl6.mobsters.events.request;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.lvl6.mobsters.eventprotos.SpeedUpBuildUserConsumableEventProto.SpeedUpBuildUserConsumableRequestProto;
//import com.lvl6.mobsters.events.RequestEvent;
//
//public class SpeedUpBuildUserConsumableRequestEvent extends RequestEvent {
//  
//  private SpeedUpBuildUserConsumableRequestProto speedUpBuildUserConsumableRequestProto;
//
//  @Override
//  public void read(ByteBuffer bb) {
//    try {
//      speedUpBuildUserConsumableRequestProto = SpeedUpBuildUserConsumableRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = speedUpBuildUserConsumableRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
//  }
//  
//  public SpeedUpBuildUserConsumableRequestProto getSpeedUpBuildUserConsumableRequestProto() {
//    return speedUpBuildUserConsumableRequestProto;
//  }
//  
//} 