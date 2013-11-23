//package com.lvl6.mobsters.events.request;
//
//import java.nio.ByteBuffer;
//
//import com.google.protobuf.ByteString;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.lvl6.mobsters.eventprotos.UserDungeonStatusProto.UserDungeonStatusRequestProto;
//import com.lvl6.mobsters.events.RequestEvent;
//
//public class UserDungeonStatusRequestEvent extends RequestEvent {
//  
//  private UserDungeonStatusRequestProto userDungeonStatusRequestProto;
//
//  @Override
//  public void read(ByteBuffer bb) {
//    try {
//      userDungeonStatusRequestProto = UserDungeonStatusRequestProto.parseFrom(ByteString.copyFrom(bb));
//      playerId = userDungeonStatusRequestProto.getMup().getUserID();
//    } catch (InvalidProtocolBufferException e) {
//      e.printStackTrace();
//    }
//  }
//  
//  public UserDungeonStatusRequestProto getUserDungeonStatusRequestProto() {
//    return userDungeonStatusRequestProto;
//  }
//  
//}