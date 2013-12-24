package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.UpdateMonsterHealthRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class UpdateMonsterHealthRequestEvent extends RequestEvent {
  
  private UpdateMonsterHealthRequestProto updateMonsterHealthRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      updateMonsterHealthRequestProto = UpdateMonsterHealthRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = updateMonsterHealthRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public UpdateMonsterHealthRequestProto getUpdateMonsterHealthRequestProto() {
    return updateMonsterHealthRequestProto;
  }
  
}