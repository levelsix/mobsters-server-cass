package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventStructureProto.MoveOrRotateNormStructureRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class MoveOrRotateNormStructureRequestEvent extends RequestEvent {
  
  private MoveOrRotateNormStructureRequestProto moveOrRotateNormStructureRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      moveOrRotateNormStructureRequestProto = MoveOrRotateNormStructureRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = moveOrRotateNormStructureRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public MoveOrRotateNormStructureRequestProto getMoveOrRotateNormStructureRequestProto() {
    return moveOrRotateNormStructureRequestProto;
  }
  
}