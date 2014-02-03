package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.EvolveMonsterRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class EvolveMonsterRequestEvent extends RequestEvent {
  
  private EvolveMonsterRequestProto evolveMonsterRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      evolveMonsterRequestProto = EvolveMonsterRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = evolveMonsterRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public EvolveMonsterRequestProto getEvolveMonsterRequestProto() {
    return evolveMonsterRequestProto;
  }
  
}