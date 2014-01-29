package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventDungeonProto.ReviveInDungeonRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class ReviveInDungeonRequestEvent extends RequestEvent {
  
  private ReviveInDungeonRequestProto reviveInDungeonRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      reviveInDungeonRequestProto = ReviveInDungeonRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = reviveInDungeonRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public ReviveInDungeonRequestProto getReviveInDungeonRequestProto() {
    return reviveInDungeonRequestProto;
  }
  
}