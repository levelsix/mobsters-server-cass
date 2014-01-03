package com.lvl6.mobsters.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.mobsters.eventprotos.EventMonsterProto.AddMonsterToBattleTeamRequestProto;
import com.lvl6.mobsters.events.RequestEvent;

public class AddMonsterToBattleTeamRequestEvent extends RequestEvent {
  
  private AddMonsterToBattleTeamRequestProto addMonsterToBattleTeamRequestProto;

  @Override
  public void read(ByteBuffer bb) {
    try {
      addMonsterToBattleTeamRequestProto = AddMonsterToBattleTeamRequestProto.parseFrom(ByteString.copyFrom(bb));
      playerId = addMonsterToBattleTeamRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }
  
  public AddMonsterToBattleTeamRequestProto getAddMonsterToBattleTeamRequestProto() {
    return addMonsterToBattleTeamRequestProto;
  }
  
}