package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.LevelUpEventProto.LevelUpRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class LevelUpRequestEvent extends RequestEvent {
  
  private LevelUpRequestProto levelUpRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      levelUpRequestProto = LevelUpRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = levelUpRequestProto.getMup().getUserID();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public LevelUpRequestProto getLevelUpRequestProto() {
    return levelUpRequestProto;
  }
  
}