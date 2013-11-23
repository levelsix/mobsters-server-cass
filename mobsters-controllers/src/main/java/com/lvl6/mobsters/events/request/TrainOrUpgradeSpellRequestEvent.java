//package com.lvl6.mobsters.events.request;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.lvl6.mobsters.eventprotos.TrainOrUpgradeSpellEventProto.TrainOrUpgradeSpellRequestProto;
//import com.lvl6.mobsters.events.RequestEvent;
//
//public class TrainOrUpgradeSpellRequestEvent extends RequestEvent {
//  
//  private TrainOrUpgradeSpellRequestProto trainOrUpgradeSpellRequestProto;
//
//  @Override
//  public void read(ByteBuffer bb) {
//    try {
//      trainOrUpgradeSpellRequestProto = TrainOrUpgradeSpellRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = trainOrUpgradeSpellRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
//  }
//  
//  public TrainOrUpgradeSpellRequestProto getTrainOrUpgradeSpellRequestProto() {
//    return trainOrUpgradeSpellRequestProto;
//  }
//  
//}