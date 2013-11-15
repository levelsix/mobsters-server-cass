package com.lvl6.aoc2.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.aoc2.events.RequestEvent;
import com.lvl6.aoc2.eventprotos.UserDungeonStatusProto.UserDungeonStatusRequestProto;

public class UserDungeonStatusRequestEvent extends RequestEvent {
  
  private UserDungeonStatusRequestProto userDungeonStatusRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      userDungeonStatusRequestProto = UserDungeonStatusRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = userDungeonStatusRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public UserDungeonStatusRequestProto getUserDungeonStatusRequestProto() {
    return userDungeonStatusRequestProto;
  }
  
}